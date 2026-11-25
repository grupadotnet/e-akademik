import { createContext, useContext, useState } from "react";
import { clearToken, getTokenSync, saveToken } from "./session";
import { logout } from "./client";

type SessionValue = {
  token: string | null;
  signIn: (token: string) => Promise<void>;
  signOut: () => Promise<void>;
};

const SessionContext = createContext<SessionValue | null>(null);

export const SessionProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const [token, setToken] = useState<string | null>(() => getTokenSync());

  const signIn = async (newToken: string) => {
    await saveToken(newToken);
    setToken(newToken);
  };

  const signOut = async () => {
    try {
      await logout();
    } catch {
      // no network shouldnt block local logout
    }
    await clearToken();
    setToken(null);
  };
  return (
    <SessionContext.Provider value={{ token, signIn, signOut }}>
      {children}
    </SessionContext.Provider>
  );
};

export function useSession() {
  const ctx = useContext(SessionContext);
  if (!ctx) throw new Error("useSession must be used inside SessionProvider");
  return ctx;
}
