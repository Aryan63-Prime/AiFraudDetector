import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";

import {
  AlertStatus,
  listAlerts,
  updateAlertStatus
} from "../api/admin";
import { AlertsTable } from "../components/AlertsTable";
import { TopBar } from "../components/TopBar";
import { useSession } from "../hooks/useSession";

const PAGE_SIZE = 20;
const statusFilters: (AlertStatus | "ALL")[] = ["ALL", "OPEN", "ACKNOWLEDGED", "RESOLVED"];

export function AlertsPage() {
  const { session } = useSession();
  const queryClient = useQueryClient();
  const [statusFilter, setStatusFilter] = useState<AlertStatus | "ALL">("OPEN");
  const [page, setPage] = useState(0);

  const token = session?.token ?? "";
  const statusQueryParam = useMemo(() => (statusFilter === "ALL" ? undefined : statusFilter), [
    statusFilter
  ]);

  const alertsQuery = useQuery({
    queryKey: ["alerts", { status: statusQueryParam, page }],
    queryFn: () => listAlerts(token, statusQueryParam, page, PAGE_SIZE),
    enabled: Boolean(token)
  });

  const updateMutation = useMutation({
    mutationFn: (input: { id: string; status: AlertStatus }) =>
      updateAlertStatus(token, input.id, input.status),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["alerts"] });
    }
  });

  const totalPages = alertsQuery.data?.totalPages ?? 0;

  function handleStatusChange(newStatus: AlertStatus | "ALL") {
    setStatusFilter(newStatus);
    setPage(0);
  }

  return (
    <div className="min-h-screen bg-slate-950">
      <TopBar />

      <main className="mx-auto max-w-6xl px-6 pb-16 pt-10">
        <div className="mb-8 flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div>
            <h1 className="text-3xl font-semibold text-slate-100">Fraud Alerts</h1>
            <p className="text-sm text-slate-400">
              Review streaming decisions from the fraud service and triage high-risk activity.
            </p>
          </div>

          <div className="flex flex-wrap items-center gap-3">
            <label className="text-sm font-medium text-slate-300" htmlFor="statusFilter">
              Status
            </label>
            <select
              id="statusFilter"
              className="rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-100 focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-400/40"
              value={statusFilter}
              onChange={(event) => handleStatusChange(event.target.value as AlertStatus | "ALL")}
            >
              {statusFilters.map((option) => (
                <option key={option} value={option}>
                  {option === "ALL" ? "All" : option.toLowerCase()}
                </option>
              ))}
            </select>
          </div>
        </div>

        <AlertsTable
          queryState={alertsQuery}
          onUpdateStatus={(id, status) => updateMutation.mutate({ id, status })}
        />

        {totalPages > 1 ? (
          <div className="mt-8 flex items-center justify-between text-sm text-slate-300">
            <button
              type="button"
              className="rounded border border-slate-700 px-3 py-1.5 text-xs uppercase tracking-wide text-slate-200 disabled:opacity-40"
              onClick={() => setPage((prev) => Math.max(prev - 1, 0))}
              disabled={page === 0}
            >
              Previous
            </button>
            <p>
              Page {page + 1} of {totalPages}
            </p>
            <button
              type="button"
              className="rounded border border-slate-700 px-3 py-1.5 text-xs uppercase tracking-wide text-slate-200 disabled:opacity-40"
              onClick={() => setPage((prev) => Math.min(prev + 1, totalPages - 1))}
              disabled={page >= totalPages - 1}
            >
              Next
            </button>
          </div>
        ) : null}
      </main>
    </div>
  );
}
