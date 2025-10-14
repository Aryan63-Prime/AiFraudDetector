import { apiClient } from "./client";
export async function login(request) {
    const { data } = await apiClient.post("/api/v1/admin/session", request);
    return data;
}
export async function listAlerts(token, status, page = 0, size = 20) {
    const { data } = await apiClient.get("/api/v1/admin/alerts", {
        params: {
            ...(status ? { status } : {}),
            page,
            size
        },
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    return data;
}
export async function updateAlertStatus(token, id, status) {
    const { data } = await apiClient.patch(`/api/v1/admin/alerts/${id}/status`, { status }, {
        headers: {
            Authorization: `Bearer ${token}`
        }
    });
    return data;
}
