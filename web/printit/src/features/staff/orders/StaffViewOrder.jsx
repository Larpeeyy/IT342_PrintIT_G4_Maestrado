import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  Clock3,
  Printer,
  PackageCheck,
  CheckCircle2,
  FileText,
  Download,
} from "lucide-react";
import StaffTopbar from "../../../shared/components/StaffTopbar";
import { getStaffOrderById, updateStaffOrderStatus } from "../../../shared/services/api";
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
      setOrder(res.data || null);
      setStatus(res.data?.status || "Pending");
    } catch (error) {
      console.error("Failed to load order:", error);
      alert(
        error?.response?.data?.message ||
          error?.message ||
          "Failed to load order."
      );
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
      alert(
        error?.response?.data?.message ||
          error?.message ||
          "Failed to update order."
      );
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
      alert(
        error?.response?.data?.message ||
          error?.message ||
          "Failed to mark order as completed."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDownloadFile = () => {
    if (order?.fileUrl) {
      window.open(order.fileUrl, "_blank");
    } else {
      alert("File download is not available.");
    }
  };

  const formatDate = (value) => {
    if (!value) return "-";

    return new Date(value).toLocaleDateString("en-US", {
      month: "long",
      day: "numeric",
      year: "numeric",
    });
  };

  const getStepClass = (step) => {
    const steps = ["Pending", "Printing", "Ready for Pickup", "Completed"];
    const currentIndex = steps.indexOf(order?.status || "Pending");
    const stepIndex = steps.indexOf(step);

    if (stepIndex < currentIndex) return `staff-progress-step done ${getStepColorClass(step)}`;
    if (stepIndex === currentIndex) return `staff-progress-step active ${getStepColorClass(step)}`;

    return "staff-progress-step";
  };

  const getStepColorClass = (step) => {
    if (step === "Pending") return "progress-yellow";
    if (step === "Printing") return "progress-blue";
    if (step === "Ready for Pickup") return "progress-green";
    if (step === "Completed") return "progress-gray";

    return "progress-gray";
  };

  const getStatusClass = (value) => {
    if (value === "Pending") return "staff-order-status-yellow";
    if (value === "Printing") return "staff-order-status-blue";
    if (value === "Ready for Pickup") return "staff-order-status-green";
    if (value === "Completed") return "staff-order-status-gray";

    return "staff-order-status-gray";
  };

  return (
    <div className="staff-page">
      <StaffTopbar activeTab="orders" />

      <main className="staff-view-order-content">
        <button
          className="staff-back-btn"
          type="button"
          onClick={() => navigate("/staff/orders")}
        >
          <ArrowLeft size={16} />
          <span>Back to Orders Queue</span>
        </button>

        {loading ? (
          <p className="staff-view-message">Loading order details...</p>
        ) : !order ? (
          <p className="staff-view-message">Order not found.</p>
        ) : (
          <>
            <section className="staff-view-header">
              <h1>Order {order.orderCode || `ORD-${order.id}`}</h1>
              <p>Submitted on {formatDate(order.createdAt)}</p>
            </section>

            <section className="staff-progress-card">
              <div className={getStepClass("Pending")}>
                <div className="staff-progress-icon">
                  <Clock3 size={18} />
                </div>
                <span>Pending</span>
              </div>

              <div className="staff-progress-line"></div>

              <div className={getStepClass("Printing")}>
                <div className="staff-progress-icon">
                  <Printer size={18} />
                </div>
                <span>Printing</span>
              </div>

              <div className="staff-progress-line"></div>

              <div className={getStepClass("Ready for Pickup")}>
                <div className="staff-progress-icon">
                  <PackageCheck size={18} />
                </div>
                <span>Ready</span>
              </div>

              <div className="staff-progress-line"></div>

              <div className={getStepClass("Completed")}>
                <div className="staff-progress-icon">
                  <CheckCircle2 size={18} />
                </div>
                <span>Completed</span>
              </div>
            </section>

            <section className="staff-view-grid">
              <div className="staff-view-card">
                <h3>File Preview</h3>

                <div className="staff-file-box">
                  <div className="staff-file-icon">
                    <FileText size={40} />
                  </div>
                  <strong>{order.fileName || "-"}</strong>
                  <small>Preview not available</small>
                </div>

                <button
                  className="staff-download-btn"
                  type="button"
                  onClick={handleDownloadFile}
                >
                  <Download size={16} />
                  <span>Download File</span>
                </button>
              </div>

              <div className="staff-view-card">
                <h3>Order Information</h3>

                <div className="staff-info-list">
                  <div>
                    <span>Student Name</span>
                    <strong>{order.studentName || "-"}</strong>
                  </div>

                  <div>
                    <span>Email</span>
                    <strong>{order.email || "-"}</strong>
                  </div>

                  <div>
                    <span>File Name</span>
                    <strong>{order.fileName || "-"}</strong>
                  </div>

                  <div>
                    <span>Paper Size</span>
                    <strong>{order.paperSize || "-"}</strong>
                  </div>

                  <div>
                    <span>Color Option</span>
                    <strong>{order.colorMode || "-"}</strong>
                  </div>

                  <div>
                    <span>Copies</span>
                    <strong>{order.copies || 0}</strong>
                  </div>

                  <div>
                    <span>Status</span>
                    <strong
                      className={`staff-order-status-pill ${getStatusClass(
                        order.status
                      )}`}
                    >
                      {order.status || "Pending"}
                    </strong>
                  </div>

                  <div>
                    <span>Total</span>
                    <strong>
                      P {Number(order.totalAmount || 0).toFixed(2)}
                    </strong>
                  </div>
                </div>
              </div>
            </section>

            <section className="staff-update-card">
              <h3>Update Order Status</h3>

              <div className="staff-status-form">
                <label htmlFor="orderStatus">Status</label>
                <select
                  id="orderStatus"
                  value={status}
                  onChange={(e) => setStatus(e.target.value)}
                >
                  <option value="Pending">Pending</option>
                  <option value="Printing">Printing</option>
                  <option value="Ready for Pickup">Ready for Pickup</option>
                  <option value="Completed">Completed</option>
                </select>
              </div>

              <div className="staff-status-actions">
                <button
                  className="staff-save-btn"
                  type="button"
                  onClick={handleSaveStatus}
                  disabled={saving}
                >
                  {saving ? "Saving..." : "Save Status"}
                </button>

                <button
                  className="staff-complete-btn"
                  type="button"
                  onClick={handleComplete}
                  disabled={saving}
                >
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