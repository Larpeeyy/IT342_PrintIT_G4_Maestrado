import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import StaffTopbar from "../../components/StaffTopbar";
import { getStaffDashboard } from "../../services/api";
import "./StaffDashboard.css";

function StaffDashboard() {
  const navigate = useNavigate();
  const [summary, setSummary] = useState({
    pendingOrders: 0,
    printingOrders: 0,
    readyForPickupOrders: 0,
    completedToday: 0,
    recentOrders: [],
  });

  useEffect(() => {
    const loadSummary = async () => {
      try {
        const res = await getStaffDashboard();
        setSummary(res.data || {});
      } catch (error) {
        console.error("Failed to load staff dashboard:", error);
        alert(
          error?.response?.data?.message ||
            error?.message ||
            "Failed to load staff dashboard."
        );
      }
    };

    loadSummary();
  }, []);

  return (
    <div className="staff-page">
      <StaffTopbar activeTab="dashboard" />

      <main className="staff-content">
        <section className="staff-heading">
          <h1>Staff Dashboard</h1>
          <p>Manage and process student print requests.</p>
        </section>

        <section className="staff-stats-grid">
          <div className="staff-stat-card">
            <span>Pending Orders</span>
            <h2>{summary.pendingOrders || 0}</h2>
          </div>

          <div className="staff-stat-card">
            <span>Printing Orders</span>
            <h2>{summary.printingOrders || 0}</h2>
          </div>

          <div className="staff-stat-card">
            <span>Ready for Pickup</span>
            <h2>{summary.readyForPickupOrders || 0}</h2>
          </div>

          <div className="staff-stat-card">
            <span>Completed Today</span>
            <h2>{summary.completedToday || 0}</h2>
          </div>
        </section>

        <section className="staff-table-card">
          <div className="staff-table-header">
            <div>
              <h3>Recent Orders</h3>
              <p>Latest student submissions</p>
            </div>

            <button onClick={() => navigate("/staff/orders")}>View All</button>
          </div>

          <table className="staff-table">
            <thead>
              <tr>
                <th>File</th>
                <th>Student</th>
                <th>Copies</th>
                <th>Status</th>
                <th>Action</th>
              </tr>
            </thead>

            <tbody>
              {(summary.recentOrders || []).map((order) => (
                <tr key={order.id}>
                  <td>{order.fileName}</td>
                  <td>{order.studentName}</td>
                  <td>{order.copies}</td>
                  <td>{order.status}</td>
                  <td>
                    <button
                      className="staff-view-link"
                      onClick={() => navigate(`/staff/orders/${order.id}`)}
                    >
                      View
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default StaffDashboard;