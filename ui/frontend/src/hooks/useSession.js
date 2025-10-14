import { useMemo } from "react";
import { useSessionContext } from "../providers/SessionProvider";
export function useSession() {
    const ctx = useSessionContext();
    return useMemo(() => ({
        session: ctx.session,
        isAuthenticated: ctx.isAuthenticated,
        login: ctx.login,
        logout: ctx.logout
    }), [ctx]);
}
