import Constants from "expo-constants";
import { Platform } from "react-native";

const API_PORT = 8088;

function devHost(): string {
  const host = Constants.expoConfig?.hostUri?.split(":")[0];
  if (host && host !== "localhost" && host !== "127.0.0.1") return host;
  return Platform.OS === "android" ? "10.0.2.2" : "localhost";
}

const override = process.env.EXPO_PUBLIC_API_URL?.trim();
export const API_URL = override
  ? override.replace(/\/$/, "")
  : `http://${devHost()}:${API_PORT}`;
