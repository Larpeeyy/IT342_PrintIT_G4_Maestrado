import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Clock3,
  Printer,
  PackageCheck,
  BadgeCheck,
  Eye,
} from "lucide-react";
import { getStaffDashboard } from "../../../shared/services/api";
import "./StaffDashboard.css";
import StaffTopbar from "../../../shared/components/StaffTopbar";

function StaffDashboard() {
  const navigate = useNavigate();

  const [dashboard, setDashboard] = useState({
    pendingOrders: 0,
    printingOrders: 0,
    readyForPickupOrders: 0,
    completedToday: 0,
    recentOrders: [],
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        setLoading(true);
        const res = await getStaffDashboard();

        setDashboard({
          pendingOrders: res.data?.pendingOrders || 0,
          printingOrders: res.data?.printingOrders || 0,
          readyForPickupOrders: res.data?.readyForPickupOrders || 0,
          completedToday: res.data?.completedToday || 0,
          recentOrders: res.data?.recentOrders || [],
        });
      } catch (error) {
        console.error("Failed to load staff dashboard:", error);
        alert(
          error?.response?.data?.message ||
            error?.response?.data?.error ||
            error?.message ||
            "Failed to load staff dashboard."
        );
      } finally {
        setLoading(false);
      }
    };

    loadDashboard();
  }, []);

  const getStatusClass = (status) => {
    if (status === "Pending") return "staff-status-yellow";
    if (status === "Printing") return "staff-status-blue";
    if (status === "Ready for Pickup") return "staff-status-green";
    if (status === "Completed") return "staff-status-gray";

    return "staff-status-gray";
  };

  return (
    <div className="staff-page">
      <StaffTopbar activeTab="dashboard" />

      <main className="staff-page-content">
        <section className="staff-page-heading">
          <h1>Staff Dashboard</h1>
          <p>Manage and process student print requests.</p>
        </section>

        <section className="staff-stats-grid">
          <div className="staff-stat-card">
            <div className="staff-stat-text">
              <span>Pending Orders</span>
              <h2>{loading ? "..." : dashboard.pendingOrders}</h2>
              <p>Orders waiting to be processed</p>
            </div>

            <div className="staff-stat-icon">
              <Clock3 size={20} />
            </div>
          </div>

          <div className="staff-stat-card">
            <div className="staff-stat-text">
              <span>Printing Orders</span>
              <h2>{loading ? "..." : dashboard.printingOrders}</h2>
              <p>Orders currently being printed</p>
            </div>

            <div className="staff-stat-icon">
              <Printer size={20} />
            </div>
          </div>

          <div className="staff-stat-card">
            <div className="staff-stat-text">
              <span>Ready for Pickup</span>
              <h2>{loading ? "..." : dashboard.readyForPickupOrders}</h2>
              <p>Orders ready to claim</p>
            </div>

            <div className="staff-stat-icon">
              <PackageCheck size={20} />
            </div>
          </div>

          <div className="staff-stat-card">
            <div className="staff-stat-text">
              <span>Completed Today</span>
              <h2>{loading ? "..." : dashboard.completedToday}</h2>
              <p>Finished orders for today</p>
            </div>

            <div className="staff-stat-icon">
              <BadgeCheck size={20} />
            </div>
          </div>
        </section>

        <section className="staff-card">
          <div className="staff-card-header">
            <div>
              <h3>Recent Orders</h3>
            </div>

            <button
              className="staff-link-btn"
              type="button"
              onClick={() => navigate("/staff/orders")}
            >
              View All
            </button>
          </div>

          {loading ? (
            <p className="staff-empty-text">Loading recent orders...</p>
          ) : dashboard.recentOrders.length === 0 ? (
            <p className="staff-empty-text">No recent orders found.</p>
          ) : (
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
                {dashboard.recentOrders.map((order) => (
                  <tr key={order.id}>
                    <td>{order.fileName || "-"}</td>
                    <td>{order.studentName || "-"}</td>
                    <td>{order.copies || 0}</td>

                    <td>
                      <span
                        className={`staff-status-pill ${getStatusClass(
                          order.status
                        )}`}
                      >
                        {order.status || "Pending"}
                      </span>
                    </td>

                    <td>
                      <button
                        className="staff-view-btn staff-icon-btn"
                        type="button"
                        onClick={() => navigate(`/staff/orders/${order.id}`)}
                      >
                        <Eye size={16} />
                        <span>View</span>
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      </main>
    </div>
  );
}

export default StaffDashboard;