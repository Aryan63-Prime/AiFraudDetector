import { jsx as _jsx } from "react/jsx-runtime";
import { createContext, useCallback, useContext, useMemo, useState } from "react";
const SessionContext = createContext(undefined);
const STORAGE_KEY = "aifraud.admin.session";
function readPersistedSession() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
        return null;
    }
    try {
        const parsed = JSON.parse(raw);
        if (!parsed.token || !parsed.user) {
            return null;
        }
        return parsed;
    }
    catch (error) {
        console.warn("Failed to parse persisted session", error);
        return null;
    }
}
export function SessionProvider({ children }) {
    const [session, setSession] = useState(() => readPersistedSession());
    const login = useCallback((value) => {
        setSession(value);
        localStorage.setItem(STORAGE_KEY, JSON.stringify(value));
    }, []);
    const logout = useCallback(() => {
        setSession(null);
        localStorage.removeItem(STORAGE_KEY);
    }, []);
    const value = useMemo(() => ({
        session,
        isAuthenticated: Boolean(session?.token),
        login,
        logout
    }), [login, logout, session]);
    return _jsx(SessionContext.Provider, { value: value, children: children });
}
export function useSessionContext() {
    const ctx = useContext(SessionContext);
    if (!ctx) {
        throw new Error("useSessionContext must be used within SessionProvider");
    }
    return ctx;
}
