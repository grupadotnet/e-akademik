import { render, screen } from "@testing-library/react-native";
import StartScreen from "./start-screen";

jest.mock("expo-router", () => ({
  useRouter: () => ({ push: jest.fn() }),
}));

test("renders the login button", async () => {
  await render(<StartScreen />);
  expect(screen.getByText("Log in")).toBeTruthy();
});
