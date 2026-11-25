import { API_URL } from "./config";
import { getToken } from "./session";

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

export type LoginResult =
  | { ok: true; token: string }
  | {
      ok: false;
      reason: "bad_credentials" | "no_token" | "network" | "other";
    };

export async function login(
  email: string,
  password: string,
): Promise<LoginResult> {
  try {
    const res = await apiFetch("/api/v1/auth/login", {
      method: "POST",
      body: { email, password },
      signal: AbortSignal.timeout(8000),
    });

    if (res.status === 401) return { ok: false, reason: "bad_credentials" };
    if (!res.ok) return { ok: false, reason: "other" };

    const data = (await res.json()) as { token?: unknown };
    if (typeof data.token !== "string" || data.token.length === 0) {
      return { ok: false, reason: "no_token" };
    }
    return { ok: true, token: data.token };
  } catch {
    return { ok: false, reason: "network" };
  }
}

export async function logout() {
  return apiFetch("/api/v1/auth/logout", { method: "POST" });
}
