import { render, screen } from "@testing-library/react-native";
import StartScreen from "./start-screen";

jest.mock("expo-router", () => ({
  useRouter: () => ({ push: jest.fn(), replace: jest.fn() }),
}));

jest.mock("@/api/session", () => ({
  getToken: () => Promise.resolve(null),
}));

test("renders the login button", async () => {
  await render(<StartScreen />);
  expect(await screen.findByText("Log in")).toBeTruthy();
});
