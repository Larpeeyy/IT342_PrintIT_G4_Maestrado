import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import StaffTopbar from "../../components/StaffTopbar";
import { getStaffOrderById, updateStaffOrderStatus } from "../../services/api";
import "./StaffViewOrder.css";

function StaffViewOrder() {
  const navigate = useNavigate();
  const { orderId } = useParams();

  const [order, setOrder] = useState(null);
  const [status, setStatus] = useState("Pending");
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const loadOrder = async () => {
    try {
      setLoading(true);
      const res = await getStaffOrderById(orderId);
      setOrder(res.data);
      setStatus(res.data?.status || "Pending");
    } catch (error) {
      console.error("Failed to load order:", error);
      alert(error?.response?.data?.message || error?.message || "Failed to load order.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrder();
  }, [orderId]);

  const handleSaveStatus = async () => {
    try {
      setSaving(true);
      await updateStaffOrderStatus(orderId, { status });
      await loadOrder();
      alert("Order status updated.");
    } catch (error) {
      console.error("Update failed:", error);
      alert(error?.response?.data?.message || error?.message || "Failed to update order.");
    } finally {
      setSaving(false);
    }
  };

  const handleComplete = async () => {
    try {
      setSaving(true);
      await updateStaffOrderStatus(orderId, { status: "Completed" });
      await loadOrder();
      setStatus("Completed");
      alert("Order marked as completed.");
    } catch (error) {
      console.error("Complete failed:", error);
      alert(error?.response?.data?.message || error?.message || "Failed to mark order as completed.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="staff-page">
      <StaffTopbar activeTab="orders" />

      <main className="staff-view-order-content">
        <button className="staff-back-btn" onClick={() => navigate("/staff/orders")}>
          ← Back to Orders Queue
        </button>

        {loading ? (
          <p>Loading order details...</p>
        ) : !order ? (
          <p>Order not found.</p>
        ) : (
          <>
            <section className="staff-view-header">
              <h1>Order {order.orderCode}</h1>
              <p>
                Submitted on{" "}
                {order.createdAt ? new Date(order.createdAt).toLocaleDateString() : "-"}
              </p>
            </section>

            <section className="staff-view-grid">
              <div className="staff-view-card">
                <h3>File Preview</h3>
                <div className="staff-file-box">
                  <div className="staff-file-icon">📄</div>
                  <strong>{order.fileName}</strong>
                  <small>Preview not available</small>
                </div>
              </div>

              <div className="staff-view-card">
                <h3>Order Information</h3>
                <div className="staff-info-list">
                  <div><span>Student Name</span><strong>{order.studentName}</strong></div>
                  <div><span>Email</span><strong>{order.email}</strong></div>
                  <div><span>File Name</span><strong>{order.fileName}</strong></div>
                  <div><span>Paper Size</span><strong>{order.paperSize}</strong></div>
                  <div><span>Color Option</span><strong>{order.colorMode}</strong></div>
                  <div><span>Copies</span><strong>{order.copies}</strong></div>
                  <div><span>Status</span><strong>{order.status}</strong></div>
                  <div><span>Total</span><strong>P {Number(order.totalAmount || 0).toFixed(2)}</strong></div>
                </div>
              </div>
            </section>

            <section className="staff-view-card">
              <h3>Update Order Status</h3>

              <div className="staff-status-form">
                <label>Status</label>
                <select value={status} onChange={(e) => setStatus(e.target.value)}>
                  <option value="Pending">Pending</option>
                  <option value="Printing">Printing</option>
                  <option value="Ready for Pickup">Ready for Pickup</option>
                  <option value="Completed">Completed</option>
                </select>
              </div>

              <div className="staff-status-actions">
                <button className="staff-save-btn" onClick={handleSaveStatus} disabled={saving}>
                  {saving ? "Saving..." : "Save Status"}
                </button>

                <button className="staff-complete-btn" onClick={handleComplete} disabled={saving}>
                  Mark as Completed
                </button>
              </div>
            </section>
          </>
        )}
      </main>
    </div>
  );
}

export default StaffViewOrder;