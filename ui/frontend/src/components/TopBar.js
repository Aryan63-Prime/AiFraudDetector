import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useNavigate } from "react-router-dom";
import { useSession } from "../hooks/useSession";
export function TopBar() {
    const { session, logout } = useSession();
    const navigate = useNavigate();
    function handleLogout() {
        logout();
        navigate("/login", { replace: true });
    }
    return (_jsx("header", { className: "border-b border-slate-800 bg-slate-900/70", children: _jsxs("div", { className: "mx-auto flex max-w-6xl items-center justify-between px-6 py-4", children: [_jsxs("div", { children: [_jsx("h1", { className: "text-xl font-semibold text-slate-100", children: "AI Fraud Command Center" }), _jsx("p", { className: "text-xs text-slate-400", children: "Streaming insights from transactions and machine scoring pipeline." })] }), _jsxs("div", { className: "flex items-center gap-4", children: [_jsxs("div", { className: "text-right", children: [_jsx("p", { className: "text-sm font-medium text-slate-200", children: session?.user.username }), _jsx("p", { className: "text-xs uppercase tracking-wide text-brand-300", children: session?.user.roles.join(", ") })] }), _jsx("button", { type: "button", onClick: handleLogout, className: "rounded-lg border border-slate-700 px-4 py-2 text-xs font-semibold uppercase tracking-wide text-slate-200 transition hover:bg-slate-800", children: "Sign out" })] })] }) }));
}
