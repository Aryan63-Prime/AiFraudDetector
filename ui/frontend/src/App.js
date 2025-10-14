import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Navigate, Outlet, Route, Routes, useLocation } from "react-router-dom";
import { AlertsPage } from "./pages/AlertsPage";
import { LoginPage } from "./pages/LoginPage";
import { useSession } from "./hooks/useSession";
function RequireAuth() {
    const { isAuthenticated } = useSession();
    const location = useLocation();
    if (!isAuthenticated) {
        return _jsx(Navigate, { to: "/login", replace: true, state: { from: location } });
    }
    return _jsx(Outlet, {});
}
export default function App() {
    const { isAuthenticated } = useSession();
    return (_jsxs(Routes, { children: [_jsx(Route, { path: "/login", element: _jsx(LoginPage, {}) }), _jsxs(Route, { element: _jsx(RequireAuth, {}), children: [_jsx(Route, { path: "/", element: _jsx(Navigate, { to: "/alerts", replace: true }) }), _jsx(Route, { path: "/alerts", element: _jsx(AlertsPage, {}) })] }), _jsx(Route, { path: "*", element: _jsx(Navigate, { to: isAuthenticated ? "/alerts" : "/login", replace: true }) })] }));
}
