import { tokenStorage } from "./token";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL ?? "http://localhost:8080";

async function parseError(res: Response): Promise<string> {
  try {
    const contentType = res.headers.get("content-type");

    if (contentType?.includes("application/json")) {
      const data = await res.json();

      if (data.message) return data.message;
      if (Array.isArray(data)) return data[0];
      if (data.error) return data.error;
      if (typeof data === "string") return data;
    }

    const text = await res.text();
    return text || "요청에 실패했습니다.";
  } catch {
    return "요청에 실패했습니다.";
  }
}

async function reissueToken(): Promise<string | null> {
  try {
    const res = await fetch(`${BASE_URL}/api/users/refresh-token`, {
      method: "POST",
      credentials: "include",
    });
    if (!res.ok) return null;
    const data = await res.json();
    tokenStorage.set(data.accessToken);
    return data.accessToken;
  } catch {
    return null;
  }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const accessToken = tokenStorage.get();

  const res = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
      ...options.headers,
    },
    credentials: "include",
  });

  if (res.status === 401) {
    const newToken = await reissueToken();

    if (!newToken) {
      tokenStorage.remove();
      window.location.href = "/login";
      throw new Error("세션이 만료됐어요. 다시 로그인해 주세요.");
    }

    const retryRes = await fetch(`${BASE_URL}${path}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${newToken}`,
        ...options.headers,
      },
      credentials: "include",
    });

    if (!retryRes.ok) throw new Error(await parseError(retryRes));
    return retryRes.json();
  }

  if (!res.ok) throw new Error(await parseError(res));
  return res.json();
}

export const api = {
  get: <T>(path: string) => request<T>(path, { method: "GET" }),

  post: <T>(path: string, body: unknown) =>
    request<T>(path, { method: "POST", body: JSON.stringify(body) }),

  put: <T>(path: string, body: unknown) =>
    request<T>(path, { method: "PUT", body: JSON.stringify(body) }),

  patch: <T>(path: string, body: unknown) =>
    request<T>(path, { method: "PATCH", body: JSON.stringify(body) }),

  delete: <T>(path: string) => request<T>(path, { method: "DELETE" }),
};
