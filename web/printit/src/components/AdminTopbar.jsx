import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ChevronDown, Settings, LogOut } from "lucide-react";
import NotificationBell from "./NotificationBell";
import "./AdminTopbar.css";

function AdminTopbar({ activeTab = "" }) {
  const navigate = useNavigate();
  const menuRef = useRef(null);
  const [menuOpen, setMenuOpen] = useState(false);

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
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("printit_user");
    localStorage.removeItem("token");
    localStorage.removeItem("authToken");
    localStorage.removeItem("studentId");
    navigate("/login");
  };

  return (
    <header className="admin-topbar">
      <div className="admin-topbar-brand" onClick={() => navigate("/admin/dashboard")}>
        <div className="admin-topbar-logo"></div>
        <span className="admin-topbar-brand-text">PrintIT</span>
      </div>

      <nav className="admin-topbar-nav">
        <button
          className={activeTab === "dashboard" ? "active" : ""}
          onClick={() => navigate("/admin/dashboard")}
        >
          Dashboard
        </button>

        <button
          className={activeTab === "users" ? "active" : ""}
          onClick={() => navigate("/admin/users")}
        >
          Users
        </button>

        <button
          className={activeTab === "payments" ? "active" : ""}
          onClick={() => navigate("/admin/payments")}
        >
          Payments
        </button>

        <button
          className={activeTab === "orders" ? "active" : ""}
          onClick={() => navigate("/admin/orders")}
        >
          Orders
        </button>
      </nav>

      <div className="admin-topbar-actions">
        <NotificationBell />

        <div className="admin-profile-menu" ref={menuRef}>
          <button
            type="button"
            className="admin-profile-button"
            onClick={() => setMenuOpen((prev) => !prev)}
          >
            {user?.profileImageUrl ? (
              <img
                src={user.profileImageUrl}
                alt="Profile"
                className="admin-profile-image"
              />
            ) : (
              <span>{initials}</span>
            )}
            <ChevronDown size={14} />
          </button>

          {menuOpen && (
            <div className="admin-profile-dropdown">
              <button
                type="button"
                className="admin-dropdown-item"
                onClick={() => {
                  setMenuOpen(false);
                  navigate("/profile");
                }}
              >
                <Settings size={16} />
                <span>Profile Settings</span>
              </button>

              <button
                type="button"
                className="admin-dropdown-item logout"
                onClick={handleLogout}
              >
                <LogOut size={16} />
                <span>Logout</span>
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}

export default AdminTopbar;