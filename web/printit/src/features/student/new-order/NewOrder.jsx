import { useMemo, useState } from "react";
import { Check } from "lucide-react";
import { useNavigate } from "react-router-dom";
import { createPrintOrderWithFile } from "../../../shared/services/api";
import StudentTopbar from "../../../shared/components/StudentTopbar";
import "./NewOrder.css";

function NewOrder() {
  const navigate = useNavigate();
  const maxFileSize = 50 * 1024 * 1024;

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
  const [currentStep, setCurrentStep] = useState(1);

  const handleFileChange = (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const lowerName = file.name.toLowerCase();
    const valid = lowerName.endsWith(".pdf") || lowerName.endsWith(".docx");

    if (!valid) {
      alert("Only PDF and DOCX files are allowed.");
      e.target.value = "";
      return;
    }

    if (file.size > maxFileSize) {
      alert("File must not exceed 50 MB.");
      e.target.value = "";
      return;
    }

    setSelectedFile(file);
  };

  const handleNextStep = () => {
    if (currentStep === 1 && !selectedFile) {
      alert("Please upload a PDF or DOCX file first.");
      return;
    }

    if (currentStep === 2 && (!copies || Number(copies) < 1)) {
      alert("Copies must be at least 1.");
      return;
    }

    setCurrentStep((prev) => Math.min(prev + 1, 3));
  };

  const handlePreviousStep = () => {
    setCurrentStep((prev) => Math.max(prev - 1, 1));
  };

  const handleSubmit = async () => {
    if (!user?.email) {
      alert("You are not logged in properly. Please login again.");
      navigate("/login");
      return;
    }

    if (!selectedFile) {
      alert("Please upload a PDF or DOCX file first.");
      return;
    }

    if (selectedFile.size > maxFileSize) {
      alert("File must not exceed 50 MB.");
      return;
    }

    try {
      setLoading(true);

      await createPrintOrderWithFile({
        email: user.email,
        file: selectedFile,
        paperSize,
        colorMode,
        copies: Number(copies),
      });

      alert("Order submitted successfully.");
      navigate("/student/orders");
    } catch (error) {
      console.error("Create order error:", error);
      alert(
        error?.response?.status === 413
          ? "File must not exceed 50 MB."
          : error?.response?.data?.message ||
              error?.response?.data?.error ||
              error?.message ||
              "Failed to submit order."
      );
    } finally {
      setLoading(false);
    }
  };

  const getStepState = (step) => {
    if (step < currentStep) return "done";
    if (step === currentStep) return "active";
    return "idle";
  };

  const renderStepCircle = (step) => {
    const state = getStepState(step);

    if (state === "done") {
      return (
        <div className="step-number done">
          <Check size={16} />
        </div>
      );
    }

    return (
      <div className={`step-number ${state === "active" ? "active" : "light"}`}>
        {step}
      </div>
    );
  };

  const total =
    (colorMode === "Color" ? 5 : 2) * Number(copies || 0);

  return (
    <div className="new-order-page">
      <StudentTopbar activeTab="new-order" />

      <main className="new-order-content">
        <h1>New Print Order</h1>
        <p>Upload your files and configure print settings.</p>

        <div className="new-order-steps">
          <div className="step-item">
            {renderStepCircle(1)}
            <div>
              <strong>Upload Files</strong>
              <span>Add your documents</span>
            </div>
          </div>

          <div className={`step-line ${currentStep > 1 ? "done" : ""}`} />

          <div className="step-item">
            {renderStepCircle(2)}
            <div>
              <strong>Print Settings</strong>
              <span>Configure options</span>
            </div>
          </div>

          <div className={`step-line ${currentStep > 2 ? "done" : ""}`} />

          <div className="step-item">
            {renderStepCircle(3)}
            <div>
              <strong>Review & Submit</strong>
              <span>Confirm your order</span>
            </div>
          </div>
        </div>

        {currentStep === 1 && (
          <section className="upload-card">
            <h3>Upload Your Documents</h3>
            <p>Drag and drop PDF files or click to browse</p>

            <label className="upload-box">
              <input
                type="file"
                accept=".pdf,.docx"
                onChange={handleFileChange}
                hidden
              />
              <div className="upload-icon">⇪</div>
              <h4>{selectedFile ? selectedFile.name : "Drop your PDF or DOCX files here"}</h4>
              <span>
                {selectedFile
                  ? "File selected successfully"
                  : "or click to browse from your computer"}
              </span>
              <small>Only PDF and DOCX files are accepted</small>
            </label>

            <div className="new-order-footer">
              <button className="ghost-btn" onClick={handlePreviousStep} disabled>
                ← Previous
              </button>
              <button className="primary-btn" onClick={handleNextStep}>
                Next →
              </button>
            </div>
          </section>
        )}

        {currentStep === 2 && (
          <section className="upload-card">
            <h3>Print Settings</h3>
            <p>Configure how you want your documents printed</p>

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

            <div className="new-order-footer">
              <button className="ghost-btn" onClick={handlePreviousStep}>
                ← Previous
              </button>
              <button className="primary-btn" onClick={handleNextStep}>
                Next →
              </button>
            </div>
          </section>
        )}

        {currentStep === 3 && (
          <section className="upload-card">
            <h3>Review Your Order</h3>
            <p>Please confirm the details before submitting</p>

            <div className="review-block">
              <strong>Files (1)</strong>
              <div className="review-file">{selectedFile?.name}</div>
            </div>

            <div className="review-block">
              <strong>Print Settings</strong>
              <div className="review-grid">
                <div>
                  <span>Paper Size</span>
                  <strong>{paperSize}</strong>
                </div>
                <div>
                  <span>Color Mode</span>
                  <strong>{colorMode}</strong>
                </div>
                <div>
                  <span>Copies</span>
                  <strong>{copies}</strong>
                </div>
              </div>
            </div>

            <div className="review-block">
              <strong>Price Summary</strong>
              <div className="review-price-row">
                <span>{colorMode === "Color" ? "P5/page" : "P2/page"} × {copies} copie(s)</span>
                <strong>P {total}.00</strong>
              </div>
              <div className="review-price-row total">
                <span>Total</span>
                <strong>P {total}.00</strong>
              </div>
            </div>

            <div className="new-order-footer">
              <button className="ghost-btn" onClick={handlePreviousStep}>
                ← Previous
              </button>
              <button className="primary-btn" onClick={handleSubmit} disabled={loading}>
                {loading ? "Submitting..." : "Submit Order"}
              </button>
            </div>
          </section>
        )}
      </main>
    </div>
  );
}

export default NewOrder;
