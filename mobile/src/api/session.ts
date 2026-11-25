import * as SecureStore from "expo-secure-store";

const KEY = "jwt_token";

export async function saveToken(token: string): Promise<void> {
  await SecureStore.setItemAsync(KEY, token);
}

export async function getToken(): Promise<string | null> {
  return SecureStore.getItemAsync(KEY);
}

export function getTokenSync(): string | null {
  return SecureStore.getItem(KEY);
}

export async function clearToken(): Promise<void> {
  await SecureStore.deleteItemAsync(KEY);
}
