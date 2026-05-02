import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export const registerUser = (data) => api.post("/api/auth/register", data);
export const loginUser = (data) => api.post("/api/auth/login", data);

export const getStudentDashboard = (email) =>
  api.get("/api/student/dashboard", { params: { email } });

export const createPrintOrder = (data) =>
  api.post("/api/student/orders", data);

export const getStudentOrders = (email) =>
  api.get("/api/student/orders", { params: { email } });

export const getStudentOrderById = (id, email) =>
  api.get(`/api/student/orders/${id}`, { params: { email } });

export const getStudentPayments = (email) =>
  api.get("/api/student/payments", { params: { email } });

export const getProfile = (email) =>
  api.get("/api/profile", { params: { email } });

export const updateProfile = (data) =>
  api.put("/api/profile", data);

export const changePassword = (data) =>
  api.put("/api/profile/change-password", data);

export default api;