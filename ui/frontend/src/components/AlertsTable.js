import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Fragment } from "react";
import { StatusBadge } from "./StatusBadge";
const statusActions = {
    OPEN: [
        { label: "Acknowledge", status: "ACKNOWLEDGED" },
        { label: "Resolve", status: "RESOLVED" }
    ],
    ACKNOWLEDGED: [{ label: "Resolve", status: "RESOLVED" }],
    RESOLVED: []
};
export function AlertsTable({ queryState, onUpdateStatus }) {
    if (queryState.isLoading) {
        return (_jsx("div", { className: "flex h-64 items-center justify-center rounded-xl border border-slate-800 bg-slate-900/40", children: _jsx("p", { className: "text-sm text-slate-400", children: "Loading alerts\u2026" }) }));
    }
    if (queryState.isError) {
        return (_jsxs("div", { className: "rounded-xl border border-red-500/40 bg-red-500/10 p-6 text-sm text-red-200", children: [_jsx("p", { children: "We couldn't load alerts right now. Please retry shortly." }), _jsx("button", { type: "button", className: "mt-4 rounded border border-red-400 px-3 py-1 text-xs uppercase tracking-wide text-red-100", onClick: () => queryState.refetch(), children: "Retry" })] }));
    }
    const alerts = queryState.data?.content ?? [];
    if (!alerts.length) {
        return (_jsx("div", { className: "flex h-64 items-center justify-center rounded-xl border border-slate-800 bg-slate-900/40", children: _jsxs("div", { className: "text-center", children: [_jsx("p", { className: "text-lg font-semibold text-slate-200", children: "All clear!" }), _jsx("p", { className: "mt-1 text-sm text-slate-400", children: "There are no alerts matching this filter." })] }) }));
    }
    return (_jsx("div", { className: "overflow-hidden rounded-xl border border-slate-800 bg-slate-900/40", children: _jsxs("table", { className: "min-w-full divide-y divide-slate-800 text-left text-sm text-slate-200", children: [_jsx("thead", { className: "bg-slate-900/60 text-xs uppercase tracking-wide text-slate-400", children: _jsxs("tr", { children: [_jsx("th", { className: "px-5 py-3 font-medium", children: "Transaction" }), _jsx("th", { className: "px-5 py-3 font-medium", children: "Risk" }), _jsx("th", { className: "px-5 py-3 font-medium", children: "Recommendation" }), _jsx("th", { className: "px-5 py-3 font-medium", children: "Evaluated" }), _jsx("th", { className: "px-5 py-3 font-medium", children: "Status" }), _jsx("th", { className: "px-5 py-3 font-medium", children: "Actions" })] }) }), _jsx("tbody", { className: "divide-y divide-slate-800", children: alerts.map((alert) => (_jsx(Fragment, { children: _jsxs("tr", { className: "hover:bg-slate-800/30", children: [_jsxs("td", { className: "px-5 py-4", children: [_jsx("p", { className: "font-semibold text-slate-100", children: alert.transactionId }), _jsxs("p", { className: "text-xs text-slate-400", children: ["Alert #", alert.id.slice(0, 8)] })] }), _jsx("td", { className: "px-5 py-4", children: _jsxs("div", { className: "text-slate-100", children: [_jsx("p", { className: "text-sm font-semibold", children: alert.riskScore.toFixed(2) }), _jsx("p", { className: "text-xs uppercase tracking-wide text-slate-400", children: alert.riskLevel })] }) }), _jsx("td", { className: "px-5 py-4 text-sm text-slate-200", children: alert.recommendation }), _jsx("td", { className: "px-5 py-4 text-xs text-slate-400", children: new Date(alert.evaluatedAt).toLocaleString() }), _jsx("td", { className: "px-5 py-4", children: _jsx(StatusBadge, { status: alert.status }) }), _jsx("td", { className: "px-5 py-4", children: _jsx("div", { className: "flex flex-wrap gap-2", children: statusActions[alert.status].length ? (statusActions[alert.status].map((action) => (_jsx("button", { type: "button", onClick: () => onUpdateStatus(alert.id, action.status), className: "rounded border border-brand-500 px-3 py-1 text-xs font-semibold uppercase tracking-wide text-brand-200 hover:bg-brand-500/20", disabled: queryState.isFetching, children: action.label }, action.status)))) : (_jsx("span", { className: "text-xs text-slate-500", children: "No actions" })) }) })] }) }, alert.id))) })] }) }));
}
