import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import OAuthSuccess from "./pages/OAuthSuccess";
import ProfileSettings from "./pages/ProfileSettings";

import AdminDashboard from "./pages/admin/AdminDashboard";
import StaffDashboard from "./pages/staff/StaffDashboard";

import StudentDashboard from "./pages/student/StudentDashboard";
import NewOrder from "./pages/student/NewOrder";
import Orders from "./pages/student/Orders";
import Payments from "./pages/student/Payments";

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
        <Route path="/staff/dashboard" element={<StaffDashboard />} />

        <Route path="/student/home" element={<StudentDashboard />} />
        <Route path="/student/new-order" element={<NewOrder />} />
        <Route path="/student/orders" element={<Orders />} />
        <Route path="/student/payments" element={<Payments />} />
      </Routes>
    </Router>
  );
}

export default App;