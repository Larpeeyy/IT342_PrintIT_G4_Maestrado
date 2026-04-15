import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getStudentDashboard } from "../../services/api";
import "./StudentDashboard.css";

function StudentDashboard() {
  const navigate = useNavigate();
  const [dashboard, setDashboard] = useState({
    totalOrders: 0,
    pendingOrders: 0,
    readyForPickupOrders: 0,
    totalSpent: 0,
    recentOrders: [],
  });

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  const firstName = user?.fullName ? user.fullName.split(" ")[0] : "Student";
  const initials = user?.fullName
    ? user.fullName
        .split(" ")
        .map((part) => part[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
    : "JD";

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        if (!user?.email) return;
        const res = await getStudentDashboard(user.email);
        setDashboard(res.data);
      } catch (error) {
        console.error("Failed to load dashboard:", error);
      }
    };

    loadDashboard();
  }, [user]);

  const handleLogout = () => {
    localStorage.removeItem("printit_user");
    localStorage.removeItem("token");
    localStorage.removeItem("authToken");
    localStorage.removeItem("studentId");
    navigate("/login");
  };

  const getStatusClass = (status) => {
    if (status === "Ready for Pickup") return "status-green";
    if (status === "Printing") return "status-blue";
    if (status === "Pending Payment") return "status-yellow";
    if (status === "Pending") return "status-yellow";
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
      <header className="student-ui-navbar">
        <div className="student-ui-brand">
          <div className="student-ui-logo"></div>
          <div className="student-ui-brand-text">PrintIT</div>
        </div>

        <nav className="student-ui-nav">
          <button className="student-ui-nav-link active" onClick={() => navigate("/student/home")}>
            Dashboard
          </button>
          <button className="student-ui-nav-link" onClick={() => navigate("/student/new-order")}>
            + New Order
          </button>
          <button className="student-ui-nav-link" onClick={() => navigate("/student/orders")}>
            Orders
          </button>
          <button className="student-ui-nav-link" onClick={() => navigate("/student/payments")}>
            Payments
          </button>
        </nav>

        <div className="student-ui-top-actions">
          <button className="student-ui-bell" type="button"></button>
          <div className="student-ui-avatar">{initials}</div>
        </div>
      </header>

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
              <h2>{dashboard.totalOrders}</h2>
              <p>All time orders</p>
            </div>
            <div className="student-ui-stat-icon"></div>
          </div>

          <div className="student-ui-stat-card">
            <div>
              <span>Pending Orders</span>
              <h2>{dashboard.pendingOrders}</h2>
              <p>Awaiting processing</p>
            </div>
            <div className="student-ui-stat-icon"></div>
          </div>

          <div className="student-ui-stat-card">
            <div>
              <span>Ready for Pickup</span>
              <h2>{dashboard.readyForPickupOrders}</h2>
              <p>Claim your prints</p>
            </div>
            <div className="student-ui-stat-icon"></div>
          </div>

          <div className="student-ui-stat-card">
            <div>
              <span>Total Spent</span>
              <h2>P {Number(dashboard.totalSpent || 0).toFixed(2)}</h2>
              <p>This semester</p>
            </div>
            <div className="student-ui-stat-icon"></div>
          </div>
        </section>

        <section className="student-ui-bottom-grid">
          <div className="student-ui-panel large">
            <div className="student-ui-panel-header">
              <div>
                <h3>Recent Orders</h3>
                <p>Your latest print orders</p>
              </div>
              <button className="student-ui-link-btn" onClick={() => navigate("/student/orders")}>
                View all →
              </button>
            </div>

            <div className="student-ui-order-list">
              {dashboard.recentOrders.length === 0 ? (
                <p>No orders yet.</p>
              ) : (
                dashboard.recentOrders.map((order) => (
                  <div key={order.id} className="student-ui-order-item">
                    <div>
                      <h4>{order.fileName}</h4>
                      <p>{order.orderCode} • {formatDate(order.createdAt)}</p>
                    </div>

                    <div className="student-ui-order-right">
                      <span className={`student-ui-status ${getStatusClass(order.status)}`}>
                        {order.status}
                      </span>
                      <strong>P {Number(order.totalAmount).toFixed(2)}</strong>
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
                <p>Common tasks</p>
              </div>
            </div>

            <div className="student-ui-action-list">
              <button onClick={() => navigate("/student/new-order")}>Create New Order</button>
              <button onClick={() => navigate("/student/orders")}>View All Orders</button>
              <button onClick={() => navigate("/student/payments")}>Payment History</button>
              <button onClick={handleLogout}>Logout</button>
            </div>
          </div>
        </section>

        <section className="student-ui-alert">
          <div>
            <h4>Track Your Latest Orders</h4>
            <p>Check your current request status and payment record anytime.</p>
          </div>

          <button onClick={() => navigate("/student/orders")}>View Details</button>
        </section>
      </main>
    </div>
  );
}

export default StudentDashboard;