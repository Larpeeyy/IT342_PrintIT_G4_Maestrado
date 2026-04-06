import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import "./StudentDashboard.css";

function StudentDashboard() {
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
    <div className="student-dashboard-page">
      <header className="student-dashboard-navbar">
        <div className="student-dashboard-brand-wrap">
          <div className="student-dashboard-brand-icon">🖨</div>
          <div className="student-dashboard-brand-text">PrintIT</div>
        </div>

        <div className="student-dashboard-nav-actions">
          <button className="student-dashboard-icon-btn" type="button">
            🔔
            <span className="student-dashboard-notif-dot"></span>
          </button>

          <div className="student-dashboard-profile-badge">ST</div>

          <div className="student-dashboard-menu-wrap" ref={menuRef}>
            <button
              className="student-dashboard-icon-btn"
              type="button"
              onClick={() => setMenuOpen((prev) => !prev)}
            >
              ☰
            </button>

            {menuOpen && (
              <div className="student-dashboard-menu-dropdown">
                <button
                  className="student-dashboard-logout-btn"
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

      <main className="student-dashboard-content">
        <section className="student-dashboard-hero-card">
          <h1>Student Dashboard</h1>
          <p>Welcome to PrintIT. This page is currently for testing role-based login and navigation.</p>
        </section>

        <section className="student-dashboard-grid">
          <div className="student-dashboard-card">
            <h3>My Orders</h3>
            <p>No data yet.</p>
          </div>

          <div className="student-dashboard-card">
            <h3>Recent Requests</h3>
            <p>No data yet.</p>
          </div>

          <div className="student-dashboard-card">
            <h3>Payment Status</h3>
            <p>No data yet.</p>
          </div>

          <div className="student-dashboard-card">
            <h3>Notifications</h3>
            <p>No data yet.</p>
          </div>
        </section>
      </main>
    </div>
  );
}

export default StudentDashboard;