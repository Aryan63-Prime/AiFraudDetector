import { jsx as _jsx } from "react/jsx-runtime";
import clsx from "clsx";
const badgeStyles = {
    OPEN: "bg-yellow-400/20 text-yellow-200 border-yellow-400/40",
    ACKNOWLEDGED: "bg-sky-400/20 text-sky-200 border-sky-400/40",
    RESOLVED: "bg-emerald-400/20 text-emerald-200 border-emerald-400/40"
};
export function StatusBadge({ status }) {
    return (_jsx("span", { className: clsx("inline-flex items-center rounded-full border px-2.5 py-1 text-xs font-semibold uppercase tracking-wide", badgeStyles[status]), children: status }));
}
