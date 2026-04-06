import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StaffDashboard.css";

function StaffDashboard() {
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef(null);

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

  return (
    <div className="staff-dashboard-page">
      <header className="staff-dashboard-navbar">
        <div className="staff-dashboard-brand-wrap">
          <div className="staff-dashboard-brand-icon">🖨</div>
          <div className="staff-dashboard-brand-text">PrintIT</div>
        </div>

        <div className="staff-dashboard-nav-actions">
          <button className="staff-dashboard-icon-btn" type="button">
            🔔
            <span className="staff-dashboard-notif-dot"></span>
          </button>

          <div className="staff-dashboard-profile-badge">SF</div>

          <div className="staff-dashboard-menu-wrap" ref={menuRef}>
            <button
              className="staff-dashboard-icon-btn"
              type="button"
              onClick={() => setMenuOpen((prev) => !prev)}
            >
              ☰
            </button>

            {menuOpen && (
              <div className="staff-dashboard-menu-dropdown">
                <button
                  className="staff-dashboard-logout-btn"
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

      <main className="staff-dashboard-content">
        <section className="staff-dashboard-hero-card">
          <h1>Staff Dashboard</h1>
          <p>Welcome to PrintIT. This page is currently for testing role-based login and navigation.</p>
        </section>

        <section className="staff-dashboard-grid">
          <div className="staff-dashboard-card">
            <h3>Order Queue</h3>
            <p>No data yet.</p>
          </div>

          <div className="staff-dashboard-card">
            <h3>Pending Prints</h3>
            <p>No data yet.</p>
          </div>

          <div className="staff-dashboard-card">
            <h3>Ready for Pickup</h3>
            <p>No data yet.</p>
          </div>

          <div className="staff-dashboard-card">
            <h3>Notifications</h3>
            <p>No data yet.</p>
          </div>
        </section>
      </main>
    </div>
  );
}

export default StaffDashboard;