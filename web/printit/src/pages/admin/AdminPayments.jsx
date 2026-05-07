import { useEffect, useState } from "react";
import AdminTopbar from "../../components/AdminTopbar";
import { getAdminPayments } from "../../services/api";
import "./AdminPayments.css";

function AdminPayments() {
  const [payments, setPayments] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadPayments = async () => {
      try {
        setLoading(true);
        const res = await getAdminPayments();
        setPayments(res.data || []);
      } catch (error) {
        console.error("Failed to load payments:", error);
        alert(
          error?.response?.data?.message ||
            error?.message ||
            "Failed to load payments."
        );
      } finally {
        setLoading(false);
      }
    };

    loadPayments();
  }, []);

  const filteredPayments = payments.filter((payment) => {
    const target = `${payment.paymentCode || ""} ${payment.orderCode || ""} ${
      payment.studentName || ""
    } ${payment.fileName || ""} ${payment.status || ""}`.toLowerCase();

    return target.includes(search.toLowerCase());
  });

  const getStatusClass = (status) => {
    if (status === "Pending") return "admin-payments-status-yellow";
    if (status === "Completed") return "admin-payments-status-gray";
    if (status === "Failed") return "admin-payments-status-red";

    return "admin-payments-status-gray";
  };

  return (
    <div className="admin-page">
      <AdminTopbar activeTab="payments" />

      <main className="admin-payments-content">
        <section className="admin-payments-heading">
          <h1>Payment Records</h1>
          <p>View and track all payment transactions.</p>
        </section>

        <section className="admin-payments-toolbar">
          <input
            type="text"
            placeholder="Search payments..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="admin-payments-card">
          {loading ? (
            <p className="admin-payments-empty-text">Loading payments...</p>
          ) : filteredPayments.length === 0 ? (
            <p className="admin-payments-empty-text">No payments found.</p>
          ) : (
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
                    <td>{payment.paymentCode || "-"}</td>
                    <td>{payment.studentName || "-"}</td>
                    <td>{payment.orderCode || "-"}</td>
                    <td>{payment.fileName || "-"}</td>
                    <td>{payment.provider || "-"}</td>

                    <td>
                      <span
                        className={`admin-payments-status-pill ${getStatusClass(
                          payment.status
                        )}`}
                      >
                        {payment.status || "-"}
                      </span>
                    </td>

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
          )}
        </section>
      </main>
    </div>
  );
}

export default AdminPayments;