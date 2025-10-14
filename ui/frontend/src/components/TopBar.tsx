import { useNavigate } from "react-router-dom";

import { useSession } from "../hooks/useSession";

export function TopBar() {
  const { session, logout } = useSession();
  const navigate = useNavigate();

  function handleLogout() {
    logout();
    navigate("/login", { replace: true });
  }

  return (
    <header className="border-b border-slate-800 bg-slate-900/70">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
        <div>
          <h1 className="text-xl font-semibold text-slate-100">AI Fraud Command Center</h1>
          <p className="text-xs text-slate-400">
            Streaming insights from transactions and machine scoring pipeline.
          </p>
        </div>

        <div className="flex items-center gap-4">
          <div className="text-right">
            <p className="text-sm font-medium text-slate-200">{session?.user.username}</p>
            <p className="text-xs uppercase tracking-wide text-brand-300">{session?.user.roles.join(", ")}</p>
          </div>
          <button
            type="button"
            onClick={handleLogout}
            className="rounded-lg border border-slate-700 px-4 py-2 text-xs font-semibold uppercase tracking-wide text-slate-200 transition hover:bg-slate-800"
          >
            Sign out
          </button>
        </div>
      </div>
    </header>
  );
}
