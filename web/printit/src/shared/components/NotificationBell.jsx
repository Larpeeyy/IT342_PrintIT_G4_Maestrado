import { useEffect, useMemo, useRef, useState } from "react";
import { Bell } from "lucide-react";
import {
  getNotifications,
  getUnreadNotificationCount,
  markNotificationAsRead,
} from "../services/api";
import "./NotificationBell.css";

function NotificationBell() {
  const wrapRef = useRef(null);

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  const [open, setOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);

  const loadUnreadCount = async () => {
    try {
      if (!user?.email) return;
      const res = await getUnreadNotificationCount(user.email);
      setUnreadCount(res.data?.count || 0);
    } catch (error) {
      console.error("Unread count error:", error);
    }
  };

  const loadNotifications = async () => {
    try {
      if (!user?.email) return;
      setLoading(true);
      const res = await getNotifications(user.email);
      setNotifications(res.data || []);
    } catch (error) {
      console.error("Notification fetch error:", error);
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUnreadCount();
  }, [user]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (wrapRef.current && !wrapRef.current.contains(event.target)) {
        setOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleToggle = async () => {
    const nextOpen = !open;
    setOpen(nextOpen);

    if (nextOpen) {
      await loadNotifications();
      await loadUnreadCount();
    }
  };

  const handleNotificationClick = async (notificationId, isRead) => {
    try {
      if (!isRead) {
        await markNotificationAsRead(notificationId);
      }
      await loadNotifications();
      await loadUnreadCount();
    } catch (error) {
      console.error("Mark read error:", error);
    }
  };

  const formatTime = (value) => {
    if (!value) return "";
    return new Date(value).toLocaleString("en-US", {
      month: "short",
      day: "numeric",
      hour: "numeric",
      minute: "2-digit",
    });
  };

  return (
    <div className="notification-bell-wrap" ref={wrapRef}>
      <button className="notification-bell-btn" type="button" onClick={handleToggle}>
        <Bell size={18} strokeWidth={2.2} />
        {unreadCount > 0 && <span className="notification-bell-dot"></span>}
      </button>

      {open && (
        <div className="notification-dropdown">
          <div className="notification-dropdown-header">
            <h4>Notifications</h4>
          </div>

          {loading ? (
            <p className="notification-empty">Loading notifications...</p>
          ) : notifications.length === 0 ? (
            <p className="notification-empty">No notifications yet.</p>
          ) : (
            <div className="notification-list">
              {notifications.map((item) => (
                <button
                  key={item.id}
                  type="button"
                  className={`notification-item ${item.isRead ? "read" : "unread"}`}
                  onClick={() => handleNotificationClick(item.id, item.isRead)}
                >
                  <strong>{item.title}</strong>
                  <span>{item.message}</span>
                  <small>{formatTime(item.createdAt)}</small>
                </button>
              ))}
            </div>
          )}
        </div>
      )}
    </div>
  );
}

export default NotificationBell;