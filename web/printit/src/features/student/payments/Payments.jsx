import { useEffect, useMemo, useState } from "react";
import { getStudentPayments } from "../../../shared/services/api";
import StudentTopbar from "../../../shared/components/StudentTopbar";
import "./Payments.css";

function Payments() {
  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("All Status");
  const [payments, setPayments] = useState([]);

  useEffect(() => {
    const loadPayments = async () => {
      try {
        if (!user?.email) return;
        const res = await getStudentPayments(user.email);
        setPayments(res.data || []);
      } catch (error) {
        console.error("Failed to load payments:", error);
      }
    };

    loadPayments();
  }, [user]);

  const filteredPayments = payments.filter((payment) => {
    const searchTarget =
      `${payment.paymentCode || ""} ${payment.orderCode || ""} ${payment.fileName || ""}`.toLowerCase();

    const matchesSearch = searchTarget.includes(search.toLowerCase());
    const matchesStatus =
      statusFilter === "All Status" || payment.status === statusFilter;

    return matchesSearch && matchesStatus;
  });

  const getStatusClass = (status) => {
    if (status === "Completed") return "payment-status-green";
    if (status === "Pending") return "payment-status-yellow";
    return "payment-status-gray";
  };

  const formatDate = (value) => {
    if (!value) return "";
    return new Date(value).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  const totalSpent = filteredPayments
    .filter((payment) => payment.status === "Completed")
    .reduce((sum, payment) => sum + Number(payment.amount || 0), 0);

  return (
    <div className="payments-page">
      <StudentTopbar activeTab="payments" />

      <main className="payments-content">
        <h1>Payment History</h1>
        <p>View all your payment transactions.</p>

        <section className="payments-stats">
          <div className="payments-stat-card">
            <div>
              <span>Total Spent</span>
              <h2>P {totalSpent.toFixed(2)}</h2>
              <p>Total completed payments</p>
            </div>
            <div className="payments-stat-icon"></div>
          </div>

          <div className="payments-stat-card">
            <div>
              <span>Transactions</span>
              <h2>{payments.length}</h2>
              <p>Total payment records</p>
            </div>
            <div className="payments-stat-icon"></div>
          </div>

          <div className="payments-stat-card">
            <div>
              <span>Average Order</span>
              <h2>P {payments.length ? (totalSpent / payments.length).toFixed(2) : "0.00"}</h2>
              <p>Average payment value</p>
            </div>
            <div className="payments-stat-icon"></div>
          </div>
        </section>

        <section className="payments-toolbar">
          <input
            type="text"
            placeholder="Search by payment ID, order ID, or description..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />

          <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
            <option>All Status</option>
            <option>Completed</option>
            <option>Pending</option>
          </select>
        </section>

        <section className="payments-table-card">
          <div className="payments-table-top">
            <div>
              <h3>Transactions</h3>
              <p>{filteredPayments.length} transactions found</p>
            </div>
          </div>

          <table className="payments-table">
            <thead>
              <tr>
                <th>Transaction ID</th>
                <th>Order</th>
                <th>Method</th>
                <th>Status</th>
                <th>Date</th>
                <th>Amount</th>
              </tr>
            </thead>

            <tbody>
              {filteredPayments.map((payment) => (
                <tr key={payment.id}>
                  <td>{payment.paymentCode}</td>
                  <td>
                    <div className="payment-order-cell">
                      <strong>{payment.orderCode}</strong>
                      <span>{payment.fileName}</span>
                    </div>
                  </td>
                  <td>{payment.provider}</td>
                  <td>
                    <span className={`payments-status ${getStatusClass(payment.status)}`}>
                      {payment.status}
                    </span>
                  </td>
                  <td>{formatDate(payment.createdAt)}</td>
                  <td>P {Number(payment.amount || 0).toFixed(2)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default Payments;