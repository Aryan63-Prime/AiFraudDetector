import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { login } from "../api/admin";
import { useSession } from "../hooks/useSession";
export function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const { login: setSession } = useSession();
    const [username, setUsername] = useState("admin");
    const [password, setPassword] = useState("changeme");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const from = location.state?.from?.pathname ?? "/alerts";
    async function handleSubmit(event) {
        event.preventDefault();
        setLoading(true);
        setError(null);
        try {
            const session = await login({ username, password });
            setSession(session);
            navigate(from, { replace: true });
        }
        catch (err) {
            console.error("Failed to login", err);
            setError("Unable to authenticate. Please check your credentials.");
        }
        finally {
            setLoading(false);
        }
    }
    return (_jsx("div", { className: "flex min-h-screen items-center justify-center bg-slate-950", children: _jsxs("div", { className: "w-full max-w-md rounded-xl border border-slate-800 bg-slate-900/70 p-8 shadow-xl", children: [_jsx("h1", { className: "text-2xl font-semibold text-slate-50", children: "AI Fraud Admin" }), _jsx("p", { className: "mt-2 text-sm text-slate-400", children: "Sign in with your operator account to review and triage fraud alerts." }), _jsxs("form", { onSubmit: handleSubmit, className: "mt-8 space-y-6", children: [_jsxs("div", { children: [_jsx("label", { className: "text-sm font-medium text-slate-200", htmlFor: "username", children: "Username" }), _jsx("input", { id: "username", name: "username", type: "text", autoComplete: "username", className: "mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-slate-100 focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-500/50", value: username, onChange: (event) => setUsername(event.target.value), required: true })] }), _jsxs("div", { children: [_jsx("label", { className: "text-sm font-medium text-slate-200", htmlFor: "password", children: "Password" }), _jsx("input", { id: "password", name: "password", type: "password", autoComplete: "current-password", className: "mt-2 w-full rounded-lg border border-slate-700 bg-slate-800 px-3 py-2 text-slate-100 focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-500/50", value: password, onChange: (event) => setPassword(event.target.value), required: true })] }), error ? _jsx("p", { className: "text-sm text-red-400", children: error }) : null, _jsx("button", { type: "submit", disabled: loading, className: "flex w-full items-center justify-center rounded-lg bg-brand-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-300 disabled:cursor-not-allowed disabled:bg-brand-700", children: loading ? "Signing in…" : "Sign in" })] }), _jsx("p", { className: "mt-6 text-xs text-slate-500", children: "This prototype uses a mocked authentication flow. Replace with SSO or IAM provider before production rollout." })] }) }));
}
