import { getToken } from "@/features/user/user-session";
import { API_URL } from "./config";

type Options = {
  method?: string;
  body?: unknown;
  signal?: AbortSignal;
};

export async function apiFetch(
  path: string,
  { method = "GET", body, signal }: Options = {},
) {
  const headers: Record<string, string> = {};

  const token = await getToken();
  if (token) headers["Authorization"] = `Bearer ${token}`;

  if (body !== undefined) headers["Content-Type"] = "application/json";

  return fetch(`${API_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
    credentials: "omit",
    signal,
  });
}
