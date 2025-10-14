import { Navigate, Outlet, Route, Routes, useLocation } from "react-router-dom";

import { AlertsPage } from "./pages/AlertsPage";
import { LoginPage } from "./pages/LoginPage";
import { useSession } from "./hooks/useSession";

function RequireAuth() {
  const { isAuthenticated } = useSession();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  return <Outlet />;
}

export default function App() {
  const { isAuthenticated } = useSession();

  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route element={<RequireAuth />}>
        <Route path="/" element={<Navigate to="/alerts" replace />} />
        <Route path="/alerts" element={<AlertsPage />} />
      </Route>
      <Route
        path="*"
        element={<Navigate to={isAuthenticated ? "/alerts" : "/login"} replace />}
      />
    </Routes>
  );
}
