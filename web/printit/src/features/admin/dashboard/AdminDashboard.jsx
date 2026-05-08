import { useEffect, useMemo, useState } from "react";
import {
  Users,
  GraduationCap,
  UserCheck,
  Clock3,
} from "lucide-react";
import AdminTopbar from "../../../shared/components/AdminTopbar";
import {
  getAdminDashboard,
  approveStaffRequest,
  rejectStaffRequest,
} from "../../../shared/services/api";
import "./AdminDashboard.css";

function AdminDashboard() {
  const [summary, setSummary] = useState({
    totalUsers: 0,
    totalStudents: 0,
    approvedStaff: 0,
    pendingStaff: 0,
    pendingStaffRequests: [],
    ordersPerDay: [],
    revenuePerMonth: [],
  });

  const [loading, setLoading] = useState(true);

  const loadSummary = async () => {
    try {
      setLoading(true);
      const res = await getAdminDashboard();

      setSummary({
        totalUsers: res.data?.totalUsers || 0,
        totalStudents: res.data?.totalStudents || 0,
        approvedStaff: res.data?.approvedStaff || 0,
        pendingStaff: res.data?.pendingStaff || 0,
        pendingStaffRequests: res.data?.pendingStaffRequests || [],
        ordersPerDay: res.data?.ordersPerDay || [],
        revenuePerMonth: res.data?.revenuePerMonth || [],
      });
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

  const ordersChart = useMemo(() => {
    return summary.ordersPerDay.map((item) => ({
      label: item.label,
      value: Number(item.value || 0),
    }));
  }, [summary.ordersPerDay]);

  const revenueChart = useMemo(() => {
    return summary.revenuePerMonth.map((item) => ({
      label: item.label,
      value: Number(item.value || 0),
    }));
  }, [summary.revenuePerMonth]);

  const getLineChartPointObjects = (data) => {
    if (!data || data.length === 0) return [];

    const width = 420;
    const height = 180;
    const paddingX = 24;
    const paddingY = 24;
    const maxValue = Math.max(...data.map((item) => item.value), 1);

    return data.map((item, index) => {
      const x =
        paddingX +
        (index * (width - paddingX * 2)) / Math.max(data.length - 1, 1);

      const y =
        height -
        paddingY -
        (item.value / maxValue) * (height - paddingY * 2);

      return {
        ...item,
        x,
        y,
        left: `${(x / width) * 100}%`,
        top: `${(y / height) * 100}%`,
      };
    });
  };

  const lineChartPoints = useMemo(() => {
    return getLineChartPointObjects(ordersChart);
  }, [ordersChart]);

  const linePolylinePoints = useMemo(() => {
    return lineChartPoints.map((point) => `${point.x},${point.y}`).join(" ");
  }, [lineChartPoints]);

  const getBarHeight = (value, data) => {
    const maxValue = Math.max(...data.map((item) => item.value), 1);
    return `${Math.max((value / maxValue) * 150, value > 0 ? 12 : 0)}px`;
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
            <div className="admin-stat-text">
              <span>Total Users</span>
              <h2>{loading ? "..." : summary.totalUsers}</h2>
              <p>All registered accounts</p>
            </div>

            <div className="admin-stat-icon">
              <Users size={22} />
            </div>
          </div>

          <div className="admin-stat-card">
            <div className="admin-stat-text">
              <span>Students</span>
              <h2>{loading ? "..." : summary.totalStudents}</h2>
              <p>Approved student accounts</p>
            </div>

            <div className="admin-stat-icon">
              <GraduationCap size={22} />
            </div>
          </div>

          <div className="admin-stat-card">
            <div className="admin-stat-text">
              <span>Approved Staff</span>
              <h2>{loading ? "..." : summary.approvedStaff}</h2>
              <p>Allowed to access staff dashboard</p>
            </div>

            <div className="admin-stat-icon">
              <UserCheck size={22} />
            </div>
          </div>

          <div className="admin-stat-card">
            <div className="admin-stat-text">
              <span>Pending Staff Requests</span>
              <h2>{loading ? "..." : summary.pendingStaff}</h2>
              <p>Requires admin action</p>
            </div>

            <div className="admin-stat-icon">
              <Clock3 size={22} />
            </div>
          </div>
        </section>

        <section className="admin-charts-grid">
          <div className="admin-chart-card">
            <h3>Orders Per Day</h3>

            {loading ? (
              <p className="admin-chart-empty">Loading chart...</p>
            ) : (
              <div className="admin-line-chart">
                <div className="admin-line-chart-area">
                  <svg viewBox="0 0 420 180" preserveAspectRatio="none">
                    <line x1="24" y1="156" x2="396" y2="156" />
                    <line x1="24" y1="24" x2="24" y2="156" />

                    <line x1="24" y1="24" x2="396" y2="24" className="grid-line" />
                    <line x1="24" y1="68" x2="396" y2="68" className="grid-line" />
                    <line x1="24" y1="112" x2="396" y2="112" className="grid-line" />

                    <polyline points={linePolylinePoints} />

                    {lineChartPoints.map((point) => (
                      <circle
                        key={point.label}
                        cx={point.x}
                        cy={point.y}
                        r="4"
                      />
                    ))}
                  </svg>

                  {lineChartPoints.map((point) => (
                    <div
                      key={point.label}
                      className="admin-line-hover-point"
                      style={{
                        left: point.left,
                        top: point.top,
                      }}
                    >
                      <div className="admin-chart-tooltip">
                        <strong>{point.label}</strong>
                        <span>Orders: {point.value}</span>
                      </div>
                    </div>
                  ))}
                </div>

                <div className="admin-chart-labels">
                  {ordersChart.map((item) => (
                    <span key={item.label}>{item.label}</span>
                  ))}
                </div>
              </div>
            )}
          </div>

          <div className="admin-chart-card">
            <h3>Revenue Per Month</h3>

            {loading ? (
              <p className="admin-chart-empty">Loading chart...</p>
            ) : (
              <div className="admin-bar-chart">
                {revenueChart.map((item) => (
                  <div className="admin-bar-item" key={item.label}>
                    <div className="admin-bar-track">
                      <div
                        className="admin-bar-fill"
                        style={{ height: getBarHeight(item.value, revenueChart) }}
                      ></div>

                      <div className="admin-chart-tooltip admin-bar-tooltip">
                        <strong>{item.label}</strong>
                        <span>Revenue: ₱ {item.value.toFixed(2)}</span>
                      </div>
                    </div>

                    <span>{item.label}</span>
                  </div>
                ))}
              </div>
            )}
          </div>
        </section>

        <section className="admin-panel">
          <div className="admin-panel-header">
            <div>
              <h3>Pending Staff Requests</h3>
              <p>
                Review and approve staff registrations before access is granted.
              </p>
            </div>

            <button className="admin-refresh-btn" onClick={loadSummary}>
              Refresh
            </button>
          </div>

          {loading ? (
            <p className="admin-empty-text">Loading requests...</p>
          ) : summary.pendingStaffRequests.length === 0 ? (
            <p className="admin-empty-text">No pending staff requests.</p>
          ) : (
            <div className="admin-request-list">
              {summary.pendingStaffRequests.map((staff) => (
                <div key={staff.id} className="admin-request-card">
                  <div className="admin-request-info">
                    <h4>{staff.fullName || "Unnamed Staff"}</h4>
                    <p>{staff.email || "-"}</p>
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