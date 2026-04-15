import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { getStudentOrders } from "../../services/api";
import "./Orders.css";

function Orders() {
  const navigate = useNavigate();
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("All Status");
  const [orders, setOrders] = useState([]);

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  useEffect(() => {
    const loadOrders = async () => {
      try {
        if (!user?.email) return;
        const res = await getStudentOrders(user.email);
        setOrders(res.data || []);
      } catch (error) {
        console.error("Failed to load orders:", error);
      }
    };

    loadOrders();
  }, [user]);

  const getStatusClass = (status) => {
    if (status === "Ready for Pickup") return "status-green";
    if (status === "Printing") return "status-blue";
    if (status === "Pending") return "status-yellow";
    if (status === "Cancelled") return "status-red";
    return "status-gray";
  };

  const formatDate = (value) => {
    if (!value) return "";
    return new Date(value).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  const filteredOrders = orders.filter((order) => {
    const matchesSearch =
      order.orderCode.toLowerCase().includes(search.toLowerCase()) ||
      order.fileName.toLowerCase().includes(search.toLowerCase());

    const matchesStatus =
      statusFilter === "All Status" || order.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  return (
    <div className="orders-page">
      <header className="orders-navbar">
        <div className="orders-brand" onClick={() => navigate("/student/home")}>
          <div className="orders-logo"></div>
          <div className="orders-brand-text">PrintIT</div>
        </div>

        <nav className="orders-nav">
          <button onClick={() => navigate("/student/home")}>Dashboard</button>
          <button onClick={() => navigate("/student/new-order")}>+ New Order</button>
          <button className="active" onClick={() => navigate("/student/orders")}>
            Orders
          </button>
          <button onClick={() => navigate("/student/payments")}>Payments</button>
        </nav>

        <div className="orders-user">JD</div>
      </header>

      <main className="orders-content">
        <h1>Order History</h1>
        <p>View and track all your print orders.</p>

        <section className="orders-toolbar">
          <input
            type="text"
            placeholder="Search by order ID or file name..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option>All Status</option>
            <option>Pending</option>
            <option>Printing</option>
            <option>Ready for Pickup</option>
            <option>Completed</option>
            <option>Cancelled</option>
          </select>
        </section>

        <section className="orders-table-card">
          <div className="orders-table-header">
            <div>
              <h3>Your Orders</h3>
              <p>{filteredOrders.length} orders found</p>
            </div>
          </div>

          <table className="orders-table">
            <thead>
              <tr>
                <th>Order ID</th>
                <th>File</th>
                <th>Details</th>
                <th>Status</th>
                <th>Date</th>
                <th>Amount</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {filteredOrders.map((order) => (
                <tr key={order.id}>
                  <td>{order.orderCode}</td>
                  <td>{order.fileName}</td>
                  <td>{order.paperSize}, {order.colorMode}, {order.copies} copy/copies</td>
                  <td>
                    <span className={`orders-status ${getStatusClass(order.status)}`}>
                      {order.status}
                    </span>
                  </td>
                  <td>{formatDate(order.createdAt)}</td>
                  <td>P {Number(order.totalAmount).toFixed(2)}</td>
                  <td className="orders-actions"></td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default Orders;