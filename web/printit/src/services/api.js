import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080",
});

export const registerUser = (data) => api.post("/api/auth/register", data);
export const loginUser = (data) => api.post("/api/auth/login", data);

export default api;