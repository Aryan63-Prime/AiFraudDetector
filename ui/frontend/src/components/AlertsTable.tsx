import { Fragment } from "react";
import type { UseQueryResult } from "@tanstack/react-query";

import type { AlertStatus, AlertView, PagedResponse } from "../api/admin";
import { StatusBadge } from "./StatusBadge";

type AlertsTableProps = {
  queryState: UseQueryResult<PagedResponse<AlertView>>;
  onUpdateStatus: (id: string, status: AlertStatus) => void;
};

const statusActions: Record<AlertStatus, { label: string; status: AlertStatus }[]> = {
  OPEN: [
    { label: "Acknowledge", status: "ACKNOWLEDGED" },
    { label: "Resolve", status: "RESOLVED" }
  ],
  ACKNOWLEDGED: [{ label: "Resolve", status: "RESOLVED" }],
  RESOLVED: []
};

export function AlertsTable({ queryState, onUpdateStatus }: AlertsTableProps) {
  if (queryState.isLoading) {
    return (
      <div className="flex h-64 items-center justify-center rounded-xl border border-slate-800 bg-slate-900/40">
        <p className="text-sm text-slate-400">Loading alerts…</p>
      </div>
    );
  }

  if (queryState.isError) {
    return (
      <div className="rounded-xl border border-red-500/40 bg-red-500/10 p-6 text-sm text-red-200">
        <p>We couldn&apos;t load alerts right now. Please retry shortly.</p>
        <button
          type="button"
          className="mt-4 rounded border border-red-400 px-3 py-1 text-xs uppercase tracking-wide text-red-100"
          onClick={() => queryState.refetch()}
        >
          Retry
        </button>
      </div>
    );
  }

  const alerts = queryState.data?.content ?? [];

  if (!alerts.length) {
    return (
      <div className="flex h-64 items-center justify-center rounded-xl border border-slate-800 bg-slate-900/40">
        <div className="text-center">
          <p className="text-lg font-semibold text-slate-200">All clear!</p>
          <p className="mt-1 text-sm text-slate-400">
            There are no alerts matching this filter.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="overflow-hidden rounded-xl border border-slate-800 bg-slate-900/40">
      <table className="min-w-full divide-y divide-slate-800 text-left text-sm text-slate-200">
        <thead className="bg-slate-900/60 text-xs uppercase tracking-wide text-slate-400">
          <tr>
            <th className="px-5 py-3 font-medium">Transaction</th>
            <th className="px-5 py-3 font-medium">Risk</th>
            <th className="px-5 py-3 font-medium">Recommendation</th>
            <th className="px-5 py-3 font-medium">Evaluated</th>
            <th className="px-5 py-3 font-medium">Status</th>
            <th className="px-5 py-3 font-medium">Actions</th>
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-800">
          {alerts.map((alert) => (
            <Fragment key={alert.id}>
              <tr className="hover:bg-slate-800/30">
                <td className="px-5 py-4">
                  <p className="font-semibold text-slate-100">{alert.transactionId}</p>
                  <p className="text-xs text-slate-400">Alert #{alert.id.slice(0, 8)}</p>
                </td>
                <td className="px-5 py-4">
                  <div className="text-slate-100">
                    <p className="text-sm font-semibold">{alert.riskScore.toFixed(2)}</p>
                    <p className="text-xs uppercase tracking-wide text-slate-400">{alert.riskLevel}</p>
                  </div>
                </td>
                <td className="px-5 py-4 text-sm text-slate-200">{alert.recommendation}</td>
                <td className="px-5 py-4 text-xs text-slate-400">
                  {new Date(alert.evaluatedAt).toLocaleString()}
                </td>
                <td className="px-5 py-4">
                  <StatusBadge status={alert.status} />
                </td>
                <td className="px-5 py-4">
                  <div className="flex flex-wrap gap-2">
                    {statusActions[alert.status].length ? (
                      statusActions[alert.status].map((action) => (
                        <button
                          key={action.status}
                          type="button"
                          onClick={() => onUpdateStatus(alert.id, action.status)}
                          className="rounded border border-brand-500 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-brand-200 hover:bg-brand-500/20"
                          disabled={queryState.isFetching}
                        >
                          {action.label}
                        </button>
                      ))
                    ) : (
                      <span className="text-xs text-slate-500">No actions</span>
                    )}
                  </div>
                </td>
              </tr>
            </Fragment>
          ))}
        </tbody>
      </table>
    </div>
  );
}
