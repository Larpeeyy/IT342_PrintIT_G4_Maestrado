import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./features/auth/Login";
import Register from "./features/auth/Register";
import OAuthSuccess from "./features/auth/OAuthSuccess";
import ProfileSettings from "./features/profile/ProfileSettings";

import AdminDashboard from "./features/admin/dashboard/AdminDashboard";
import AdminUsers from "./features/admin/users/AdminUsers";
import AdminOrders from "./features/admin/orders/AdminOrders";
import AdminPayments from "./features/admin/payments/AdminPayments";

import StaffDashboard from "./features/staff/dashboard/StaffDashboard";
import StaffOrdersQueue from "./features/staff/orders/StaffOrdersQueue";
import StaffViewOrder from "./features/staff/orders/StaffViewOrder";

import StudentDashboard from "./features/student/dashboard/StudentDashboard";
import NewOrder from "./features/student/new-order/NewOrder";
import Orders from "./features/student/orders/Orders";
import Payments from "./features/student/payments/Payments";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/oauth-success" element={<OAuthSuccess />} />
        <Route path="/profile" element={<ProfileSettings />} />

        <Route path="/admin/dashboard" element={<AdminDashboard />} />
        <Route path="/admin/users" element={<AdminUsers />} />
        <Route path="/admin/orders" element={<AdminOrders />} />
        <Route path="/admin/payments" element={<AdminPayments />} />

        <Route path="/staff/dashboard" element={<StaffDashboard />} />
        <Route path="/staff/orders" element={<StaffOrdersQueue />} />
        <Route path="/staff/orders/:orderId" element={<StaffViewOrder />} />

        <Route path="/student/home" element={<StudentDashboard />} />
        <Route path="/student/new-order" element={<NewOrder />} />
        <Route path="/student/orders" element={<Orders />} />
        <Route path="/student/payments" element={<Payments />} />
      </Routes>
    </Router>
  );
}

export default App;