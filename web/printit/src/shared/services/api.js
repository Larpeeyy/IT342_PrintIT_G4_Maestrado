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

export const createPrintOrderWithFile = ({ file, ...data }) => {
  const formData = new FormData();
  formData.append("file", file);
  formData.append("email", data.email);
  formData.append("paperSize", data.paperSize);
  formData.append("colorMode", data.colorMode);
  formData.append("copies", String(data.copies));

  return api.post("/api/student/orders/with-file", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

export const uploadPrintOrderFile = (file) => {
  const formData = new FormData();
  formData.append("file", file);

  return api.post("/api/student/orders/upload", formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};

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

export const getAdminDashboard = () =>
  api.get("/api/admin/dashboard");

export const approveStaffRequest = (userId) =>
  api.put(`/api/admin/staff/${userId}/approve`);

export const rejectStaffRequest = (userId) =>
  api.put(`/api/admin/staff/${userId}/reject`);

export const getAdminUsers = () =>
  api.get("/api/admin/users");

export const getAdminOrders = () =>
  api.get("/api/admin/orders");

export const getAdminPayments = () =>
  api.get("/api/admin/payments");

export const getStaffPendingPayments = () =>
  api.get("/api/staff/payments/pending");

export const markStaffPaymentAsPaid = (paymentId) =>
  api.put(`/api/staff/payments/${paymentId}/mark-paid`);

export const getStaffDashboard = () =>
  api.get("/api/staff/dashboard");

export const getStaffOrders = () =>
  api.get("/api/staff/orders");

export const getStaffOrderById = (orderId) =>
  api.get(`/api/staff/orders/${orderId}`);

export const updateStaffOrderStatus = (orderId, data) =>
  api.put(`/api/staff/orders/${orderId}/status`, data);

export const getStaffOrderDownloadUrl = (orderId) =>
  `${api.defaults.baseURL}/api/staff/orders/${orderId}/download`;

export const getNotifications = (email) =>
  api.get("/api/notifications", { params: { email } });

export const getUnreadNotificationCount = (email) =>
  api.get("/api/notifications/unread-count", { params: { email } });

export const markNotificationAsRead = (notificationId) =>
  api.put(`/api/notifications/${notificationId}/read`);

export default api;
