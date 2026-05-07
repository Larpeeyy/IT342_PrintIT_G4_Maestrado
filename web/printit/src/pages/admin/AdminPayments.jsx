import { useEffect, useState } from "react";
import AdminTopbar from "../../components/AdminTopbar";
import { getAdminPayments } from "../../services/api";
import "./AdminPayments.css";

function AdminPayments() {
  const [payments, setPayments] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const loadPayments = async () => {
      try {
        const res = await getAdminPayments();
        setPayments(res.data || []);
      } catch (error) {
        console.error("Failed to load payments:", error);
        alert(error?.response?.data?.message || error?.message || "Failed to load payments.");
      }
    };

    loadPayments();
  }, []);

  const filteredPayments = payments.filter((payment) => {
    const target =
      `${payment.paymentCode || ""} ${payment.orderCode || ""} ${payment.studentName || ""} ${payment.fileName || ""} ${payment.status || ""}`.toLowerCase();
    return target.includes(search.toLowerCase());
  });

  return (
    <div className="admin-page">
      <AdminTopbar activeTab="payments" />

      <main className="admin-payments-content">
        <h1>Payment Records</h1>
        <p>View and track all payment transactions.</p>

        <section className="admin-payments-toolbar">
          <input
            type="text"
            placeholder="Search payments..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="admin-payments-table-card">
          <table className="admin-payments-table">
            <thead>
              <tr>
                <th>Payment ID</th>
                <th>Student</th>
                <th>Order ID</th>
                <th>File</th>
                <th>Provider</th>
                <th>Status</th>
                <th>Date</th>
                <th>Amount</th>
              </tr>
            </thead>

            <tbody>
              {filteredPayments.map((payment) => (
                <tr key={payment.id}>
                  <td>{payment.paymentCode}</td>
                  <td>{payment.studentName}</td>
                  <td>{payment.orderCode}</td>
                  <td>{payment.fileName}</td>
                  <td>{payment.provider}</td>
                  <td>{payment.status}</td>
                  <td>
                    {payment.createdAt
                      ? new Date(payment.createdAt).toLocaleDateString()
                      : "-"}
                  </td>
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

export default AdminPayments;