const ACCESS_TOKEN_KEY = "accessToken";

export const tokenStorage = {
  get: () => {
    if (typeof window === "undefined") return null;
    return localStorage.getItem(ACCESS_TOKEN_KEY);
  },
  set: (token: string) => {
    localStorage.setItem(ACCESS_TOKEN_KEY, token);
  },
  remove: () => {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
  },
};
