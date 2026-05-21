import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { ChevronDown, LogOut, Settings } from "lucide-react";
import NotificationBell from "./NotificationBell";
import "./StaffTopbar.css";

function StaffTopbar({ activeTab = "" }) {
  const navigate = useNavigate();
  const menuRef = useRef(null);

  const [menuOpen, setMenuOpen] = useState(false);
  const [imageError, setImageError] = useState(false);

  const [user, setUser] = useState(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  });

  const initials = user?.fullName
    ? user.fullName
        .split(" ")
        .map((part) => part[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
    : "ST";

  const hasProfileImage = Boolean(user?.profileImageUrl) && !imageError;

  useEffect(() => {
    setImageError(false);
  }, [user?.profileImageUrl]);

  useEffect(() => {
    const syncUser = () => {
      try {
        setUser(JSON.parse(localStorage.getItem("printit_user")) || {});
      } catch {
        setUser({});
      }
    };

    window.addEventListener("profile-updated", syncUser);
    window.addEventListener("storage", syncUser);

    return () => {
      window.removeEventListener("profile-updated", syncUser);
      window.removeEventListener("storage", syncUser);
    };
  }, []);

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
    <header className="staff-topbar">
      <div
        className="staff-topbar-brand"
        onClick={() => navigate("/staff/dashboard")}
      >
        <div className="staff-topbar-logo"></div>
        <span className="staff-topbar-brand-text">PrintIT</span>
      </div>

      <nav className="staff-topbar-nav">
        <button
          className={activeTab === "dashboard" ? "active" : ""}
          onClick={() => navigate("/staff/dashboard")}
        >
          Dashboard
        </button>

        <button
          className={activeTab === "orders" ? "active" : ""}
          onClick={() => navigate("/staff/orders")}
        >
          Orders Queue
        </button>
      </nav>

      <div className="staff-topbar-actions">
        <NotificationBell />

        <div className="staff-profile-menu" ref={menuRef}>
          <button
            type="button"
            className={`staff-profile-button ${
              hasProfileImage ? "has-image" : ""
            }`}
            onClick={() => setMenuOpen((prev) => !prev)}
            title="Staff Menu"
          >
            {hasProfileImage ? (
              <img
                src={user.profileImageUrl}
                alt="Profile"
                className="staff-profile-image"
                onError={() => setImageError(true)}
              />
            ) : (
              <span>{initials}</span>
            )}

            <ChevronDown size={14} />
          </button>

          {menuOpen && (
            <div className="staff-profile-dropdown">
              <button
                type="button"
                className="staff-dropdown-item"
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
                className="staff-dropdown-item logout"
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

export default StaffTopbar;