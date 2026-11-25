import { fireEvent, render, screen, waitFor } from "@testing-library/react-native";
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
  signIn.mockClear();
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

test("asks for the missing field and does not call the server", async () => {
  await render(<LoginForm />);

  await submit();
  expect(screen.getByText("Enter your email")).toBeTruthy();

  await fill("  ada@example.com  ", "");
  await submit();
  expect(screen.getByText("Enter your password")).toBeTruthy();

  expect(loginMock).not.toHaveBeenCalled();
});

test("stops on an invalid email", async () => {
  await render(<LoginForm />);

  await fill("not-an-email", "password123");
  await submit();

  expect(screen.getByText("Enter a valid email")).toBeTruthy();
  expect(loginMock).not.toHaveBeenCalled();
});

test("shows a generic error for a short password and does not reveal the length rule", async () => {
  loginMock.mockResolvedValue({ ok: false, reason: "bad_credentials" });
  await render(<LoginForm />);

  await fill("ada@example.com", "abc");
  await submit();

  expect(screen.getByText("Wrong email or password")).toBeTruthy();
  expect(loginMock).toHaveBeenCalledWith("ada@example.com", "abc");
  expect(screen.queryByText(/8/)).toBeNull();
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
