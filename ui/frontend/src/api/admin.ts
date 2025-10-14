import { apiClient } from "./client";
import type { Session } from "../providers/SessionProvider";

export type LoginRequest = {
  username: string;
  password: string;
};

export type AlertStatus = "OPEN" | "ACKNOWLEDGED" | "RESOLVED";

export type AlertView = {
  id: string;
  transactionId: string;
  riskScore: number;
  riskLevel: "LOW" | "MEDIUM" | "HIGH";
  recommendation: "ALLOW" | "REVIEW" | "BLOCK";
  evaluatedAt: string;
  status: AlertStatus;
  createdAt: string;
  updatedAt: string;
};

export type PagedResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
};

export async function login(request: LoginRequest): Promise<Session> {
  const { data } = await apiClient.post<Session>("/api/v1/admin/session", request);
  return data;
}

export async function listAlerts(
  token: string,
  status?: AlertStatus,
  page = 0,
  size = 20
): Promise<PagedResponse<AlertView>> {
  const { data } = await apiClient.get<PagedResponse<AlertView>>("/api/v1/admin/alerts", {
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

export async function updateAlertStatus(
  token: string,
  id: string,
  status: AlertStatus
): Promise<AlertView> {
  const { data } = await apiClient.patch<AlertView>(
    `/api/v1/admin/alerts/${id}/status`,
    { status },
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  );
  return data;
}
