import {
  fireEvent,
  render,
  screen,
  waitFor,
} from "@testing-library/react-native";
import { useSession } from "@/features/user/providers/session-provider";
import { login } from "@/features/user/user-api";
import { LoginForm } from "./login-form";

jest.mock("@/features/user/user-api", () => ({
  login: jest.fn(),
}));

jest.mock("@/features/user/providers/session-provider", () => ({
  useSession: jest.fn(),
}));

const loginMock = jest.mocked(login);
const signIn = jest.fn(async () => {});

beforeEach(() => {
  loginMock.mockReset();
  signIn.mockReset();
  signIn.mockImplementation(async () => {});
  jest.mocked(useSession).mockReturnValue({
    token: null,
    signIn,
    signOut: async () => {},
  });
});

async function fill(email: string, password: string) {
  await fireEvent.changeText(screen.getByPlaceholderText("Email"), email);
  await fireEvent.changeText(screen.getByPlaceholderText("Password"), password);
}

async function submit() {
  await fireEvent.press(screen.getByText("Log in"));
}

test("asks for the email first when the whole form is empty", async () => {
  await render(<LoginForm />);

  await submit();

  expect(screen.getByText("Enter your email")).toBeTruthy();
  expect(screen.queryByText("Enter your password")).toBeNull();
  expect(loginMock).not.toHaveBeenCalled();
});

test("stops on an invalid email", async () => {
  await render(<LoginForm />);

  await fill("not-an-email", "password123");
  await submit();

  expect(screen.getByText("Enter a valid email")).toBeTruthy();
  expect(loginMock).not.toHaveBeenCalled();
});

test("does not validate password length client-side", async () => {
  loginMock.mockResolvedValue({ ok: false, reason: "bad_credentials" });
  await render(<LoginForm />);

  await fill("ada@example.com", "abc");
  await submit();

  expect(loginMock).toHaveBeenCalledWith("ada@example.com", "abc");
  expect(screen.getByText("Wrong email or password")).toBeTruthy();
  expect(signIn).not.toHaveBeenCalled();
});

test("trims the email and signs in with the token from the server", async () => {
  loginMock.mockResolvedValue({ ok: true, token: "jwt-1" });
  await render(<LoginForm />);

  await fill("  ada@example.com  ", "password123");
  await submit();

  expect(loginMock).toHaveBeenCalledWith("ada@example.com", "password123");
  await waitFor(() => expect(signIn).toHaveBeenCalledWith("jwt-1"));
});

test("shows a network error and does not sign in", async () => {
  loginMock.mockResolvedValue({ ok: false, reason: "network" });
  await render(<LoginForm />);

  await fill("ada@example.com", "password123");
  await submit();

  expect(screen.getByText("Cannot reach the server")).toBeTruthy();
  expect(signIn).not.toHaveBeenCalled();
});

test("unlocks the button when saving the session fails", async () => {
  loginMock.mockResolvedValue({ ok: true, token: "jwt-1" });
  signIn.mockRejectedValueOnce(new Error("store"));
  await render(<LoginForm />);

  await fill("ada@example.com", "password123");
  await submit();

  expect(screen.getByText("Could not log in")).toBeTruthy();
  expect(screen.getByText("Log in")).toBeTruthy();
});

test("disables the button while login is in flight", async () => {
  let finish: (value: { ok: true; token: string }) => void = () => {};
  loginMock.mockReturnValue(
    new Promise((resolve) => {
      finish = resolve;
    }),
  );
  await render(<LoginForm />);

  await fill("ada@example.com", "password123");
  const first = fireEvent.press(screen.getByText("Log in"));
  await waitFor(() => expect(screen.getByText("Logging in...")).toBeTruthy());
  await fireEvent.press(screen.getByText("Logging in..."));

  expect(loginMock).toHaveBeenCalledTimes(1);
  finish({ ok: true, token: "jwt-1" });
  await first;
  await waitFor(() => expect(signIn).toHaveBeenCalledWith("jwt-1"));
});
