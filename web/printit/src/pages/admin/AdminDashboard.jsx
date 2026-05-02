import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./AdminDashboard.css";

function AdminDashboard() {
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  const initials = user?.fullName
    ? user.fullName
        .split(" ")
        .map((part) => part[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
    : "AD";

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setMenuOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("printit_user");
    localStorage.removeItem("token");
    localStorage.removeItem("authToken");
    localStorage.removeItem("studentId");
    navigate("/login");
  };

  const linePoints = [
    { day: "Mon", value: 12 },
    { day: "Tue", value: 19 },
    { day: "Wed", value: 8 },
    { day: "Thu", value: 24 },
    { day: "Fri", value: 31 },
    { day: "Sat", value: 15 },
    { day: "Sun", value: 9 },
  ];

  const maxLineValue = 32;

  const barData = [
    { month: "Jan", value: 4100 },
    { month: "Feb", value: 3700 },
    { month: "Mar", value: 5000 },
    { month: "Apr", value: 4500 },
    { month: "May", value: 6100 },
    { month: "Jun", value: 5700 },
  ];

  const maxBarValue = 8000;

  const svgWidth = 700;
  const svgHeight = 260;
  const paddingLeft = 55;
  const paddingRight = 30;
  const paddingTop = 20;
  const paddingBottom = 35;

  const chartWidth = svgWidth - paddingLeft - paddingRight;
  const chartHeight = svgHeight - paddingTop - paddingBottom;

  const points = linePoints.map((item, index) => {
    const x = paddingLeft + (index * chartWidth) / (linePoints.length - 1);
    const y =
      paddingTop + chartHeight - (item.value / maxLineValue) * chartHeight;
    return { ...item, x, y };
  });

  const pathData = points
    .map((point, index) => `${index === 0 ? "M" : "L"} ${point.x} ${point.y}`)
    .join(" ");

  return (
    <div className="admin-dashboard-page">
      <header className="dashboard-navbar">
        <div className="brand-wrap">
          <div className="brand-icon">🖨</div>
          <div className="brand-text">PrintIT</div>
        </div>

        <div className="nav-actions">
          <button className="icon-btn" type="button">
            🔔
            <span className="notif-dot"></span>
          </button>

          <button
            className="profile-badge admin-profile-btn"
            type="button"
            onClick={() => navigate("/profile")}
            title="Profile Settings"
          >
            {user?.profileImageUrl ? (
              <img src={user.profileImageUrl} alt="Profile" className="admin-avatar-image" />
            ) : (
              initials
            )}
          </button>

          <div className="menu-wrap" ref={menuRef}>
            <button
              className="icon-btn menu-btn"
              type="button"
              onClick={() => setMenuOpen((prev) => !prev)}
            >
              ☰
            </button>

            {menuOpen && (
              <div className="menu-dropdown">
                <button
                  className="menu-action-btn"
                  type="button"
                  onClick={() => {
                    setMenuOpen(false);
                    navigate("/profile");
                  }}
                >
                  Profile Settings
                </button>

                <button
                  className="logout-btn"
                  type="button"
                  onClick={handleLogout}
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="dashboard-content">
        <section className="dashboard-heading">
          <h1>System Overview</h1>
          <p>Monitor platform performance and recent activity</p>
        </section>

        <section className="stats-grid">
          <div className="stat-card">
            <div>
              <p className="stat-title">Total Users</p>
              <h2>1,284</h2>
              <span className="stat-positive">+12% from last month</span>
            </div>
            <div className="stat-icon">👥</div>
          </div>

          <div className="stat-card">
            <div>
              <p className="stat-title">Total Orders</p>
              <h2>3,642</h2>
              <span className="stat-positive">+8% from last month</span>
            </div>
            <div className="stat-icon">📋</div>
          </div>

          <div className="stat-card">
            <div>
              <p className="stat-title">Revenue</p>
              <h2>$24,530</h2>
              <span className="stat-positive">+15% from last month</span>
            </div>
            <div className="stat-icon">👛</div>
          </div>

          <div className="stat-card">
            <div>
              <p className="stat-title">Pending Payments</p>
              <h2>47</h2>
              <span className="stat-negative">-3% from last month</span>
            </div>
            <div className="stat-icon">💳</div>
          </div>
        </section>

        <section className="chart-card">
          <h3>Orders Per Day</h3>

          <div className="chart-area">
            <svg viewBox={`0 0 ${svgWidth} ${svgHeight}`} className="line-chart">
              {[0, 8, 16, 24, 32].map((tick) => {
                const y =
                  paddingTop + chartHeight - (tick / maxLineValue) * chartHeight;
                return (
                  <g key={tick}>
                    <line
                      x1={paddingLeft}
                      y1={y}
                      x2={svgWidth - paddingRight}
                      y2={y}
                      className="grid-line"
                    />
                    <text x={paddingLeft - 10} y={y + 4} className="axis-label">
                      {tick}
                    </text>
                  </g>
                );
              })}

              {points.map((point) => (
                <g key={point.day}>
                  <line
                    x1={point.x}
                    y1={paddingTop}
                    x2={point.x}
                    y2={paddingTop + chartHeight}
                    className="grid-line vertical-grid"
                  />
                  <text
                    x={point.x}
                    y={svgHeight - 8}
                    textAnchor="middle"
                    className="axis-label"
                  >
                    {point.day}
                  </text>
                </g>
              ))}

              <path d={pathData} fill="none" className="line-path" />

              {points.map((point) => (
                <circle
                  key={`${point.day}-dot`}
                  cx={point.x}
                  cy={point.y}
                  r="5"
                  className="line-dot"
                />
              ))}
            </svg>
          </div>
        </section>

        <section className="chart-card">
          <h3>Revenue Per Month</h3>

          <div className="bar-chart-wrap">
            <div className="bar-chart-y-axis">
              <span>8000</span>
              <span>6000</span>
              <span>4000</span>
              <span>2000</span>
              <span>0</span>
            </div>

            <div className="bar-chart-main">
              <div className="bar-grid">
                {barData.map((item) => (
                  <div key={item.month} className="bar-item">
                    <div className="bar-column-wrap">
                      <div
                        className="bar-column"
                        style={{
                          height: `${(item.value / maxBarValue) * 240}px`,
                        }}
                      ></div>
                    </div>
                    <span className="bar-label">{item.month}</span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </section>

        <section className="activity-card">
          <h3>Recent Activity</h3>

          <table className="activity-table">
            <thead>
              <tr>
                <th>User</th>
                <th>Action</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>Maria Santos</td>
                <td>Placed new order</td>
                <td>Mar 3, 2026</td>
              </tr>
              <tr>
                <td>Juan Dela Cruz</td>
                <td>Payment received</td>
                <td>Mar 3, 2026</td>
              </tr>
              <tr>
                <td>Ana Reyes</td>
                <td>Account registered</td>
                <td>Mar 2, 2026</td>
              </tr>
              <tr>
                <td>Carlos Garcia</td>
                <td>Order completed</td>
                <td>Mar 2, 2026</td>
              </tr>
              <tr>
                <td>Beth Lim</td>
                <td>Submitted complaint</td>
                <td>Mar 1, 2026</td>
              </tr>
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default AdminDashboard;