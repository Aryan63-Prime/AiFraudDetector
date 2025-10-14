import {
  ReactNode,
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState
} from "react";

export type SessionUser = {
  username: string;
  roles: string[];
};

export type Session = {
  token: string;
  user: SessionUser;
  expiresAt: string;
};

type SessionContextValue = {
  session: Session | null;
  isAuthenticated: boolean;
  login: (session: Session) => void;
  logout: () => void;
};

const SessionContext = createContext<SessionContextValue | undefined>(undefined);

const STORAGE_KEY = "aifraud.admin.session";

function readPersistedSession(): Session | null {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as Session;
    if (!parsed.token || !parsed.user) {
      return null;
    }
    return parsed;
  } catch (error) {
    console.warn("Failed to parse persisted session", error);
    return null;
  }
}

export function SessionProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => readPersistedSession());

  const login = useCallback((value: Session) => {
    setSession(value);
    localStorage.setItem(STORAGE_KEY, JSON.stringify(value));
  }, []);

  const logout = useCallback(() => {
    setSession(null);
    localStorage.removeItem(STORAGE_KEY);
  }, []);

  const value = useMemo<SessionContextValue>(
    () => ({
      session,
      isAuthenticated: Boolean(session?.token),
      login,
      logout
    }),
    [login, logout, session]
  );

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSessionContext(): SessionContextValue {
  const ctx = useContext(SessionContext);
  if (!ctx) {
    throw new Error("useSessionContext must be used within SessionProvider");
  }
  return ctx;
}
