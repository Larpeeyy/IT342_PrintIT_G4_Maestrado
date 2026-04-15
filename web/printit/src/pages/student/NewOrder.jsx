import { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createPrintOrder } from "../../services/api";
import "./NewOrder.css";

function NewOrder() {
  const navigate = useNavigate();

  const user = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  const [selectedFile, setSelectedFile] = useState(null);
  const [paperSize, setPaperSize] = useState("A4");
  const [colorMode, setColorMode] = useState("Black & White");
  const [copies, setCopies] = useState(1);
  const [loading, setLoading] = useState(false);

  const initials = user?.fullName
    ? user.fullName
        .split(" ")
        .map((part) => part[0])
        .join("")
        .slice(0, 2)
        .toUpperCase()
    : "JD";

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];

    if (!file) return;

    const fileName = file.name.toLowerCase();
    const isValidType = fileName.endsWith(".pdf") || fileName.endsWith(".docx");

    if (!isValidType) {
      alert("Only PDF and DOCX files are allowed.");
      e.target.value = "";
      return;
    }

    setSelectedFile(file);
  };

  const handleNext = async () => {
    if (!user?.email) {
      alert("You are not logged in properly. Please login again.");
      navigate("/login");
      return;
    }

    if (!selectedFile) {
      alert("Please upload a PDF or DOCX file first.");
      return;
    }

    if (!copies || Number(copies) < 1) {
      alert("Copies must be at least 1.");
      return;
    }

    try {
      setLoading(true);

      await createPrintOrder({
        email: user.email,
        fileName: selectedFile.name,
        paperSize,
        colorMode,
        copies: Number(copies),
      });

      alert("Order submitted successfully.");
      navigate("/student/orders");
    } catch (error) {
      console.error("Create order error:", error);

      let message = "Failed to submit order.";

      if (typeof error?.response?.data === "string") {
        if (error.response.data.includes("Cannot GET /login")) {
          message =
            "Backend request was redirected to login. Check SecurityConfig and restart your backend.";
        } else {
          message = error.response.data;
        }
      } else {
        message =
          error?.response?.data?.message ||
          error?.response?.data?.error ||
          "Failed to submit order.";
      }

      alert(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="new-order-page">
      <header className="new-order-navbar">
        <div className="new-order-brand" onClick={() => navigate("/student/home")}>
          <div className="new-order-logo"></div>
          <div className="new-order-brand-text">PrintIT</div>
        </div>

        <nav className="new-order-nav">
          <button onClick={() => navigate("/student/home")}>Dashboard</button>
          <button className="active" onClick={() => navigate("/student/new-order")}>
            + New Order
          </button>
          <button onClick={() => navigate("/student/orders")}>Orders</button>
          <button onClick={() => navigate("/student/payments")}>Payments</button>
        </nav>

        <div className="new-order-user">{initials}</div>
      </header>

      <main className="new-order-content">
        <h1>New Print Order</h1>
        <p>Upload your files and configure print settings.</p>

        <div className="new-order-steps">
          <div className="step-item active">
            <div className="step-number">1</div>
            <div>
              <strong>Upload Files</strong>
              <span>Add your documents</span>
            </div>
          </div>

          <div className="step-line" />

          <div className="step-item">
            <div className="step-number light">2</div>
            <div>
              <strong>Print Settings</strong>
              <span>Configure options</span>
            </div>
          </div>

          <div className="step-line" />

          <div className="step-item">
            <div className="step-number light">3</div>
            <div>
              <strong>Review & Submit</strong>
              <span>Confirm your order</span>
            </div>
          </div>
        </div>

        <section className="upload-card">
          <h3>Upload Your Documents</h3>
          <p>Drag and drop PDF files or click to browse</p>

          <label className="upload-box">
            <input type="file" accept=".pdf,.docx" onChange={handleFileChange} hidden />
            <div className="upload-icon">⇪</div>
            <h4>
              {selectedFile ? selectedFile.name : "Drop your PDF or DOCX files here"}
            </h4>
            <span>
              {selectedFile
                ? "File selected successfully"
                : "or click to browse from your computer"}
            </span>
            <small>Only PDF and DOCX files are accepted</small>
          </label>

          <div style={{ marginTop: "20px", display: "grid", gap: "14px" }}>
            <div>
              <label>Paper Size</label>
              <select
                value={paperSize}
                onChange={(e) => setPaperSize(e.target.value)}
                style={{
                  width: "100%",
                  height: "44px",
                  marginTop: "6px",
                  borderRadius: "12px",
                  border: "1px solid #ddd",
                  padding: "0 12px",
                }}
              >
                <option value="A4">A4</option>
                <option value="Letter">Letter</option>
                <option value="Legal">Legal</option>
              </select>
            </div>

            <div>
              <label>Color Option</label>
              <div style={{ display: "flex", gap: "12px", marginTop: "6px" }}>
                <button
                  type="button"
                  className="primary-btn"
                  style={{
                    background: colorMode === "Black & White" ? "#9b2c3a" : "#eee",
                    color: colorMode === "Black & White" ? "#fff" : "#111",
                    flex: 1,
                  }}
                  onClick={() => setColorMode("Black & White")}
                >
                  Black & White
                </button>
                <button
                  type="button"
                  className="primary-btn"
                  style={{
                    background: colorMode === "Color" ? "#9b2c3a" : "#eee",
                    color: colorMode === "Color" ? "#fff" : "#111",
                    flex: 1,
                  }}
                  onClick={() => setColorMode("Color")}
                >
                  Color
                </button>
              </div>
            </div>

            <div>
              <label>Number of Copies</label>
              <input
                type="number"
                min="1"
                value={copies}
                onChange={(e) => setCopies(e.target.value)}
                style={{
                  width: "100%",
                  height: "44px",
                  marginTop: "6px",
                  borderRadius: "12px",
                  border: "1px solid #ddd",
                  padding: "0 12px",
                }}
              />
            </div>
          </div>
        </section>

        <div className="new-order-footer">
          <button className="ghost-btn" disabled>
            ← Previous
          </button>

          <button className="primary-btn" onClick={handleNext} disabled={loading}>
            {loading ? "Submitting..." : "Next →"}
          </button>
        </div>
      </main>
    </div>
  );
}

export default NewOrder;