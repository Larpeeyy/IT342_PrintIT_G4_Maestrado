import { useEffect, useState } from "react";
import AdminTopbar from "../../components/AdminTopbar";
import { getAdminOrders } from "../../services/api";
import "./AdminOrders.css";

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadOrders = async () => {
      try {
        setLoading(true);
        const res = await getAdminOrders();
        setOrders(res.data || []);
      } catch (error) {
        console.error("Failed to load admin orders:", error);
        alert(
          error?.response?.data?.message ||
            error?.message ||
            "Failed to load orders."
        );
      } finally {
        setLoading(false);
      }
    };

    loadOrders();
  }, []);

  const filteredOrders = orders.filter((order) => {
    const target = `${order.fileName || ""} ${order.studentName || ""} ${
      order.dateSubmitted || ""
    } ${order.status || ""}`.toLowerCase();

    return target.includes(search.toLowerCase());
  });

  const getStatusClass = (status) => {
    if (status === "Pending") return "admin-orders-status-yellow";
    if (status === "Printing") return "admin-orders-status-blue";
    if (status === "Ready for Pickup") return "admin-orders-status-green";
    if (status === "Completed") return "admin-orders-status-gray";

    return "admin-orders-status-gray";
  };

  return (
    <div className="admin-page">
      <AdminTopbar activeTab="orders" />

      <main className="admin-orders-content">
        <section className="admin-orders-heading">
          <h1>All Orders</h1>
          <p>Read-only overview of all platform orders.</p>
        </section>

        <section className="admin-orders-toolbar">
          <input
            type="text"
            placeholder="Search orders or students..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="admin-orders-card">
          {loading ? (
            <p className="admin-orders-empty-text">Loading orders...</p>
          ) : filteredOrders.length === 0 ? (
            <p className="admin-orders-empty-text">No orders found.</p>
          ) : (
            <table className="admin-orders-table">
              <thead>
                <tr>
                  <th>File Name</th>
                  <th>Student Name</th>
                  <th>Date Submitted</th>
                  <th>Copies</th>
                  <th>Status</th>
                </tr>
              </thead>

              <tbody>
                {filteredOrders.map((order) => (
                  <tr key={order.id}>
                    <td>{order.fileName || "-"}</td>
                    <td>{order.studentName || "-"}</td>
                    <td>{order.dateSubmitted || "-"}</td>
                    <td>{order.copies || 0}</td>

                    <td>
                      <span
                        className={`admin-orders-status-pill ${getStatusClass(
                          order.status
                        )}`}
                      >
                        {order.status || "-"}
                      </span>
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

export default AdminOrders;