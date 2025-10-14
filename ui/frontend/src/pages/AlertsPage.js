import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { listAlerts, updateAlertStatus } from "../api/admin";
import { AlertsTable } from "../components/AlertsTable";
import { TopBar } from "../components/TopBar";
import { useSession } from "../hooks/useSession";
const PAGE_SIZE = 20;
const statusFilters = ["ALL", "OPEN", "ACKNOWLEDGED", "RESOLVED"];
export function AlertsPage() {
    const { session } = useSession();
    const queryClient = useQueryClient();
    const [statusFilter, setStatusFilter] = useState("OPEN");
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
        mutationFn: (input) => updateAlertStatus(token, input.id, input.status),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["alerts"] });
        }
    });
    const totalPages = alertsQuery.data?.totalPages ?? 0;
    function handleStatusChange(newStatus) {
        setStatusFilter(newStatus);
        setPage(0);
    }
    return (_jsxs("div", { className: "min-h-screen bg-slate-950", children: [_jsx(TopBar, {}), _jsxs("main", { className: "mx-auto max-w-6xl px-6 pb-16 pt-10", children: [_jsxs("div", { className: "mb-8 flex flex-col gap-4 md:flex-row md:items-center md:justify-between", children: [_jsxs("div", { children: [_jsx("h1", { className: "text-3xl font-semibold text-slate-100", children: "Fraud Alerts" }), _jsx("p", { className: "text-sm text-slate-400", children: "Review streaming decisions from the fraud service and triage high-risk activity." })] }), _jsxs("div", { className: "flex flex-wrap items-center gap-3", children: [_jsx("label", { className: "text-sm font-medium text-slate-300", htmlFor: "statusFilter", children: "Status" }), _jsx("select", { id: "statusFilter", className: "rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-100 focus:border-brand-400 focus:outline-none focus:ring-2 focus:ring-brand-400/40", value: statusFilter, onChange: (event) => handleStatusChange(event.target.value), children: statusFilters.map((option) => (_jsx("option", { value: option, children: option === "ALL" ? "All" : option.toLowerCase() }, option))) })] })] }), _jsx(AlertsTable, { queryState: alertsQuery, onUpdateStatus: (id, status) => updateMutation.mutate({ id, status }) }), totalPages > 1 ? (_jsxs("div", { className: "mt-8 flex items-center justify-between text-sm text-slate-300", children: [_jsx("button", { type: "button", className: "rounded border border-slate-700 px-3 py-1.5 text-xs uppercase tracking-wide text-slate-200 disabled:opacity-40", onClick: () => setPage((prev) => Math.max(prev - 1, 0)), disabled: page === 0, children: "Previous" }), _jsxs("p", { children: ["Page ", page + 1, " of ", totalPages] }), _jsx("button", { type: "button", className: "rounded border border-slate-700 px-3 py-1.5 text-xs uppercase tracking-wide text-slate-200 disabled:opacity-40", onClick: () => setPage((prev) => Math.min(prev + 1, totalPages - 1)), disabled: page >= totalPages - 1, children: "Next" })] })) : null] })] }));
}
