import { useEffect, useState } from "react";
import AdminTopbar from "../../components/AdminTopbar";
import {
  getAdminDashboard,
  approveStaffRequest,
  rejectStaffRequest,
} from "../../services/api";
import "./AdminDashboard.css";

function AdminDashboard() {
  const [summary, setSummary] = useState({
    totalUsers: 0,
    totalStudents: 0,
    approvedStaff: 0,
    pendingStaff: 0,
    pendingStaffRequests: [],
  });

  const [loading, setLoading] = useState(true);

  const loadSummary = async () => {
    try {
      setLoading(true);
      const res = await getAdminDashboard();
      setSummary(res.data || {});
    } catch (error) {
      console.error("Failed to load admin dashboard:", error);
      alert(
        error?.response?.data?.message ||
          error?.message ||
          "Failed to load admin dashboard."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadSummary();
  }, []);

  const handleApprove = async (userId) => {
    try {
      await approveStaffRequest(userId);
      await loadSummary();
      alert("Staff approved successfully.");
    } catch (error) {
      console.error("Approve failed:", error);
      alert(
        error?.response?.data?.message ||
          error?.message ||
          "Failed to approve staff."
      );
    }
  };

  const handleReject = async (userId) => {
    try {
      await rejectStaffRequest(userId);
      await loadSummary();
      alert("Staff rejected successfully.");
    } catch (error) {
      console.error("Reject failed:", error);
      alert(
        error?.response?.data?.message ||
          error?.message ||
          "Failed to reject staff."
      );
    }
  };

  return (
    <div className="admin-page">
      <AdminTopbar activeTab="dashboard" />

      <main className="admin-content">
        <section className="admin-heading">
          <h1>Admin Dashboard</h1>
          <p>Monitor users and approve staff registration requests.</p>
        </section>

        <section className="admin-stats-grid">
          <div className="admin-stat-card">
            <span>Total Users</span>
            <h2>{summary.totalUsers || 0}</h2>
            <p>All registered accounts</p>
          </div>

          <div className="admin-stat-card">
            <span>Students</span>
            <h2>{summary.totalStudents || 0}</h2>
            <p>Approved student accounts</p>
          </div>

          <div className="admin-stat-card">
            <span>Approved Staff</span>
            <h2>{summary.approvedStaff || 0}</h2>
            <p>Allowed to access staff dashboard</p>
          </div>

          <div className="admin-stat-card">
            <span>Pending Staff Requests</span>
            <h2>{summary.pendingStaff || 0}</h2>
            <p>Requires admin action</p>
          </div>
        </section>

        <section className="admin-panel">
          <div className="admin-panel-header">
            <div>
              <h3>Pending Staff Requests</h3>
              <p>Review and approve staff registrations before access is granted.</p>
            </div>

            <button className="admin-refresh-btn" onClick={loadSummary}>
              Refresh
            </button>
          </div>

          {loading ? (
            <p className="admin-empty-text">Loading requests...</p>
          ) : !summary.pendingStaffRequests || summary.pendingStaffRequests.length === 0 ? (
            <p className="admin-empty-text">No pending staff requests.</p>
          ) : (
            <div className="admin-request-list">
              {summary.pendingStaffRequests.map((staff) => (
                <div key={staff.id} className="admin-request-card">
                  <div className="admin-request-info">
                    <h4>{staff.fullName}</h4>
                    <p>{staff.email}</p>
                    <small>Staff ID: {staff.staffId || "Not set"}</small>
                  </div>

                  <div className="admin-request-actions">
                    <button
                      className="approve-btn"
                      onClick={() => handleApprove(staff.id)}
                    >
                      Approve
                    </button>
                    <button
                      className="reject-btn"
                      onClick={() => handleReject(staff.id)}
                    >
                      Reject
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </section>
      </main>
    </div>
  );
}

export default AdminDashboard;