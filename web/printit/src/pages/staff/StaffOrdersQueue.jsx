import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import StaffTopbar from "../../components/StaffTopbar";
import { getStaffOrders } from "../../services/api";
import "./StaffOrdersQueue.css";

function StaffOrdersQueue() {
  const navigate = useNavigate();
  const [orders, setOrders] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const loadOrders = async () => {
      try {
        const res = await getStaffOrders();
        setOrders(res.data || []);
      } catch (error) {
        console.error("Failed to load staff orders:", error);
        alert(error?.response?.data?.message || error?.message || "Failed to load orders.");
      }
    };

    loadOrders();
  }, []);

  const filteredOrders = orders.filter((order) => {
    const target =
      `${order.fileName || ""} ${order.studentName || ""} ${order.status || ""}`.toLowerCase();
    return target.includes(search.toLowerCase());
  });

  return (
    <div className="staff-page">
      <StaffTopbar activeTab="orders" />

      <main className="staff-orders-content">
        <h1>Orders Queue</h1>
        <p>View and manage all print orders.</p>

        <section className="staff-orders-toolbar">
          <input
            type="text"
            placeholder="Search orders or students..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="staff-orders-table-card">
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
                  <td>{order.fileName}</td>
                  <td>{order.studentName}</td>
                  <td>
                    {order.createdAt ? new Date(order.createdAt).toLocaleDateString() : "-"}
                  </td>
                  <td>{order.copies}</td>
                  <td>{order.status}</td>
                  <td>
                    <button
                      className="staff-orders-view-btn"
                      onClick={() => navigate(`/staff/orders/${order.id}`)}
                    >
                      View Order
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default StaffOrdersQueue;