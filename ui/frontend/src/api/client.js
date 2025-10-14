import axios from "axios";
const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/";
export const apiClient = axios.create({
    baseURL: BASE_URL,
    timeout: 10000
});
apiClient.interceptors.response.use((response) => response, (error) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
        // bubble up 401 for session handling
    }
    return Promise.reject(error);
});
