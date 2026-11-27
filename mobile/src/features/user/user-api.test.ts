import { apiFetch } from "@/lib/api";
import { login } from "./user-api";

jest.mock("@/lib/api", () => ({
  apiFetch: jest.fn(),
}));

const apiFetchMock = jest.mocked(apiFetch);

function response(status: number, body?: unknown): Response {
  return {
    status,
    ok: status >= 200 && status < 300,
    json: async () => body,
  } as Response;
}

beforeEach(() => {
  apiFetchMock.mockReset();
});

test.each([400, 401])("treats HTTP %s as bad credentials", async (status) => {
  apiFetchMock.mockResolvedValue(response(status));

  await expect(login("ada@example.com", "secret")).resolves.toEqual({
    ok: false,
    reason: "bad_credentials",
  });
});

test("keeps other HTTP errors generic", async () => {
  apiFetchMock.mockResolvedValue(response(500));

  await expect(login("ada@example.com", "secret")).resolves.toEqual({
    ok: false,
    reason: "other",
  });
});

test("returns the token and posts the credentials", async () => {
  apiFetchMock.mockResolvedValue(response(200, { token: "jwt-1" }));

  await expect(login("ada@example.com", "secret")).resolves.toEqual({
    ok: true,
    token: "jwt-1",
  });
  expect(apiFetchMock).toHaveBeenCalledWith("/api/v1/auth/login", {
    method: "POST",
    body: { email: "ada@example.com", password: "secret" },
    signal: expect.any(AbortSignal),
  });
});

test.each([{}, { token: "" }, { token: 1 }])(
  "rejects a success body without a token string",
  async (body) => {
    apiFetchMock.mockResolvedValue(response(200, body));

    await expect(login("ada@example.com", "secret")).resolves.toEqual({
      ok: false,
      reason: "no_token",
    });
  },
);

test("turns a failed request into a network error", async () => {
  apiFetchMock.mockRejectedValue(new Error("offline"));

  await expect(login("ada@example.com", "secret")).resolves.toEqual({
    ok: false,
    reason: "network",
  });
});
