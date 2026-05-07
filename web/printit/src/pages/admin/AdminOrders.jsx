import { useEffect, useState } from "react";
import AdminTopbar from "../../components/AdminTopbar";
import { getAdminOrders } from "../../services/api";
import "./AdminOrders.css";

function AdminOrders() {
  const [orders, setOrders] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const loadOrders = async () => {
      try {
        const res = await getAdminOrders();
        setOrders(res.data || []);
      } catch (error) {
        console.error("Failed to load admin orders:", error);
        alert(error?.response?.data?.message || error?.message || "Failed to load orders.");
      }
    };

    loadOrders();
  }, []);

  const filteredOrders = orders.filter((order) => {
    const target =
      `${order.fileName || ""} ${order.studentName || ""} ${order.dateSubmitted || ""} ${order.status || ""}`.toLowerCase();
    return target.includes(search.toLowerCase());
  });

  return (
    <div className="admin-page">
      <AdminTopbar activeTab="orders" />

      <main className="admin-orders-content">
        <h1>All Orders</h1>
        <p>Read-only overview of all platform orders.</p>

        <section className="admin-orders-toolbar">
          <input
            type="text"
            placeholder="Search orders or students..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="admin-orders-table-card">
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
                  <td>{order.fileName}</td>
                  <td>{order.studentName}</td>
                  <td>{order.dateSubmitted}</td>
                  <td>{order.copies}</td>
                  <td>{order.status}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default AdminOrders;