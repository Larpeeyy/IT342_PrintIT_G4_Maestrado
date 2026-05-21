import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  CreditCard,
  FileText,
  PackageCheck,
  Clock3,
} from "lucide-react";
import { getStudentDashboard } from "../../../shared/services/api";
import StudentTopbar from "../../../shared/components/StudentTopbar";
import "./StudentDashboard.css";

function StudentDashboard() {
  const navigate = useNavigate();

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  const [dashboard, setDashboard] = useState({
    totalOrders: 0,
    pendingOrders: 0,
    readyForPickupOrders: 0,
    totalSpent: 0,
    recentOrders: [],
  });

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        if (!user?.email) return;
        const res = await getStudentDashboard(user.email);
        setDashboard(res.data || {});
      } catch (error) {
        console.error("Failed to load dashboard:", error);
      }
    };

    loadDashboard();
  }, [user]);

  const firstName = user?.fullName ? user.fullName.split(" ")[0] : "Student";

  const getStatusClass = (status) => {
    if (status === "Ready for Pickup") return "status-green";
    if (status === "Printing") return "status-blue";
    if (status === "Pending") return "status-yellow";
    if (status === "Completed") return "status-gray";
    return "status-gray";
  };

  const formatDate = (value) => {
    if (!value) return "";
    return new Date(value).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  return (
    <div className="student-ui-page">
      <StudentTopbar activeTab="dashboard" />

      <main className="student-ui-content">
        <div className="student-ui-heading-row">
          <div>
            <h1>Welcome back, {firstName}!</h1>
            <p>Here&apos;s what&apos;s happening with your print orders.</p>
          </div>

          <button
            className="student-ui-primary-btn"
            onClick={() => navigate("/student/new-order")}
          >
            + New Order
          </button>
        </div>

        <section className="student-ui-stats-grid">
          <div className="student-ui-stat-card">
            <div>
              <span>Total Orders</span>
              <h2>{dashboard.totalOrders || 0}</h2>
              <p>All your submitted requests</p>
            </div>
            <div className="student-ui-stat-icon">
              <FileText size={22} />
            </div>
          </div>

          <div className="student-ui-stat-card">
            <div>
              <span>Pending Orders</span>
              <h2>{dashboard.pendingOrders || 0}</h2>
              <p>Still waiting to be processed</p>
            </div>
            <div className="student-ui-stat-icon">
              <Clock3 size={22} />
            </div>
          </div>

          <div className="student-ui-stat-card">
            <div>
              <span>Ready for Pickup</span>
              <h2>{dashboard.readyForPickupOrders || 0}</h2>
              <p>Orders ready to claim</p>
            </div>
            <div className="student-ui-stat-icon">
              <PackageCheck size={22} />
            </div>
          </div>

          <div className="student-ui-stat-card">
            <div>
              <span>Total Spent</span>
              <h2>P {Number(dashboard.totalSpent || 0).toFixed(2)}</h2>
              <p>Total completed payments</p>
            </div>
            <div className="student-ui-stat-icon">
              <CreditCard size={22} />
            </div>
          </div>
        </section>

        <section className="student-ui-bottom-grid">
          <div className="student-ui-panel large">
            <div className="student-ui-panel-header">
              <div>
                <h3>Recent Orders</h3>
                <p>Your latest print submissions</p>
              </div>
              <button
                className="student-ui-link-btn"
                onClick={() => navigate("/student/orders")}
              >
                View all →
              </button>
            </div>

            <div className="student-ui-order-list">
              {!dashboard.recentOrders || dashboard.recentOrders.length === 0 ? (
                <p>No orders yet.</p>
              ) : (
                dashboard.recentOrders.map((order) => (
                  <div key={order.id} className="student-ui-order-item">
                    <div>
                      <h4>{order.fileName}</h4>
                      <p>
                        {order.orderCode} • {formatDate(order.createdAt)}
                      </p>
                    </div>

                    <div className="student-ui-order-right">
                      <span className={`student-ui-status ${getStatusClass(order.status)}`}>
                        {order.status}
                      </span>
                      <strong>P {Number(order.totalAmount || 0).toFixed(2)}</strong>
                    </div>
                  </div>
                ))
              )}
            </div>
          </div>

          <div className="student-ui-panel small">
            <div className="student-ui-panel-header">
              <div>
                <h3>Quick Actions</h3>
                <p>Go where you need fast</p>
              </div>
            </div>

            <div className="student-ui-action-list">
              <button onClick={() => navigate("/student/new-order")}>Create New Order</button>
              <button onClick={() => navigate("/student/orders")}>View Orders</button>
              <button onClick={() => navigate("/student/payments")}>View Payments</button>
            </div>
          </div>
        </section>

        <section className="student-ui-alert">
          <div>
            <h4>Track your latest orders</h4>
            <p>Check order status and payment updates from your student account.</p>
          </div>
          <button onClick={() => navigate("/student/orders")}>View Details</button>
        </section>
      </main>
    </div>
  );
}

export default StudentDashboard;