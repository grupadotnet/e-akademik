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
  if (token) headers["Cookie"] = `jwt_token=${token}`;

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
      reason: "bad_credentials" | "no_cookie" | "network" | "other";
    };

export async function login(
  email: string,
  password: string,
): Promise<LoginResult> {
  try {
    const res = await apiFetch("/api/auth/login", {
      method: "POST",
      body: { email, password },
      signal: AbortSignal.timeout(8000),
    });

    if (res.status === 401) return { ok: false, reason: "bad_credentials" };
    if (res.status !== 204) return { ok: false, reason: "other" };

    const header = res.headers.get("set-cookie");
    const match = header?.match(/jwt_token=([^;]+)/);
    if (!match) return { ok: false, reason: "no_cookie" };

    return { ok: true, token: match[1] };
  } catch {
    return { ok: false, reason: "network" };
  }
}

export async function logout() {
  return apiFetch("/api/auth/logout", { method: "POST" });
}
