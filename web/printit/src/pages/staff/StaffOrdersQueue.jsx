import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Eye } from "lucide-react";
import StaffTopbar from "../../components/StaffTopbar";
import { getStaffOrders } from "../../services/api";
import "./StaffOrdersQueue.css";

function StaffOrdersQueue() {
  const navigate = useNavigate();

  const [orders, setOrders] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadOrders = async () => {
      try {
        setLoading(true);
        const res = await getStaffOrders();
        setOrders(res.data || []);
      } catch (error) {
        console.error("Failed to load staff orders:", error);
        alert(
          error?.response?.data?.message ||
            error?.response?.data?.error ||
            error?.message ||
            "Failed to load orders."
        );
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, []);

  const filteredOrders = useMemo(() => {
    return orders.filter((order) => {
      const target = `${order.fileName || ""} ${order.studentName || ""} ${
        order.status || ""
      }`.toLowerCase();

      return target.includes(search.toLowerCase());
    });
  }, [orders, search]);

  const getStatusClass = (status) => {
    if (status === "Pending") return "staff-orders-status-yellow";
    if (status === "Printing") return "staff-orders-status-blue";
    if (status === "Ready for Pickup") return "staff-orders-status-green";
    if (status === "Completed") return "staff-orders-status-gray";

    return "staff-orders-status-gray";
  };

  const formatDate = (value) => {
    if (!value) return "-";

    return new Date(value).toLocaleDateString("en-US", {
      month: "numeric",
      day: "numeric",
      year: "numeric",
    });
  };

  return (
    <div className="staff-page">
      <StaffTopbar activeTab="orders" />

      <main className="staff-orders-content">
        <section className="staff-orders-heading">
          <h1>Orders Queue</h1>
          <p>View and manage all print orders.</p>
        </section>

        <section className="staff-orders-toolbar">
          <input
            type="text"
            placeholder="Search orders or students..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="staff-orders-card">
          {loading ? (
            <p className="staff-orders-empty-text">Loading orders...</p>
          ) : filteredOrders.length === 0 ? (
            <p className="staff-orders-empty-text">No orders found.</p>
          ) : (
            <table className="staff-orders-table">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>Student Name</th>
                  <th>Date Submitted</th>
                  <th>Copies</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>

              <tbody>
                {filteredOrders.map((order) => (
                  <tr key={order.id}>
                    <td>{order.fileName || "-"}</td>
                    <td>{order.studentName || "-"}</td>
                    <td>{formatDate(order.createdAt)}</td>
                    <td>{order.copies || 0}</td>

                    <td>
                      <span
                        className={`staff-orders-status-pill ${getStatusClass(
                          order.status
                        )}`}
                      >
                        {order.status || "Pending"}
                      </span>
                    </td>

                    <td>
                      <button
                        className="staff-orders-view-btn"
                        type="button"
                        onClick={() => navigate(`/staff/orders/${order.id}`)}
                      >
                        <Eye size={16} />
                        <span>View Order</span>
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

export default StaffOrdersQueue;