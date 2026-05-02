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

  const [currentStep, setCurrentStep] = useState(1);
  const [selectedFile, setSelectedFile] = useState(null);
  const [paperSize, setPaperSize] = useState("A4");
  const [colorMode, setColorMode] = useState("Black & White");
  const [copies, setCopies] = useState(1);
  const [loading, setLoading] = useState(false);

  const highlightRed = "#9b2c3a";

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

  const handleRemoveFile = () => {
    setSelectedFile(null);
  };

  const handleNext = () => {
    if (currentStep === 1) {
      if (!selectedFile) {
        alert("Please upload a PDF or DOCX file first.");
        return;
      }
      setCurrentStep(2);
      return;
    }

    if (currentStep === 2) {
      if (!copies || Number(copies) < 1) {
        alert("Copies must be at least 1.");
        return;
      }
      setCurrentStep(3);
    }
  };

  const handlePrevious = () => {
    if (currentStep > 1) {
      setCurrentStep((prev) => prev - 1);
    }
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

  const getStepCircleContent = (step) => {
    if (currentStep > step) return "✓";
    return step;
  };

  const getStepCircleClass = (step) => {
    if (currentStep > step) return "step-number completed";
    if (currentStep === step) return "step-number active";
    return "step-number light";
  };

  const getStepLineClass = (step) => {
    return currentStep > step ? "step-line completed" : "step-line";
  };

  const renderStepOne = () => (
    <section className="upload-card">
      <div className="step-card-header">
        <h3>Upload Your Documents</h3>
        <p>Drag and drop PDF files or click to browse</p>
      </div>

      <label className="upload-box modern-upload-box">
        <input type="file" accept=".pdf,.docx" onChange={handleFileChange} hidden />
        <div className="upload-icon">⇪</div>
        <h4>Drop your PDF or DOCX files here</h4>
        <span>or click to browse from your computer</span>
        <small>Only PDF and DOCX files are accepted</small>
      </label>

      {selectedFile && (
        <div className="uploaded-files-wrap">
          <label className="uploaded-files-label">Uploaded Files (1)</label>

          <div className="uploaded-file-item">
            <div className="uploaded-file-left">
              <div className="uploaded-file-icon">📄</div>
              <div>
                <h5>{selectedFile.name}</h5>
                <p>Ready for printing</p>
              </div>
            </div>

            <button
              type="button"
              className="remove-file-btn"
              onClick={handleRemoveFile}
            >
              ×
            </button>
          </div>
        </div>
      )}
    </section>
  );

  const renderStepTwo = () => (
    <section className="upload-card">
      <div className="step-card-header">
        <h3>Print Settings</h3>
        <p>Configure how you want your documents printed</p>
      </div>

      <div className="settings-grid">
        <div className="settings-block">
          <label>Paper Size</label>
          <div className="option-row">
            {["A4", "Letter", "Legal"].map((size) => (
              <button
                key={size}
                type="button"
                className={`option-pill ${paperSize === size ? "selected" : ""}`}
                onClick={() => setPaperSize(size)}
              >
                {size}
              </button>
            ))}
          </div>
        </div>

        <div className="settings-block">
          <label>Color Mode</label>
          <div className="option-row">
            <button
              type="button"
              className={`option-pill option-pill-large ${
                colorMode === "Black & White" ? "selected" : ""
              }`}
              onClick={() => setColorMode("Black & White")}
            >
              <strong>Black & White</strong>
              <span>P2/page</span>
            </button>

            <button
              type="button"
              className={`option-pill option-pill-large ${
                colorMode === "Color" ? "selected" : ""
              }`}
              onClick={() => setColorMode("Color")}
            >
              <strong>Color</strong>
              <span>P5/page</span>
            </button>
          </div>
        </div>

        <div className="settings-block settings-block-full">
          <label>Number of Copies</label>
          <input
            className="modern-input"
            type="number"
            min="1"
            value={copies}
            onChange={(e) => setCopies(e.target.value)}
          />
        </div>
      </div>
    </section>
  );

  const renderStepThree = () => (
    <section className="upload-card">
      <div className="step-card-header">
        <h3>Review Your Order</h3>
        <p>Please confirm the details before submitting</p>
      </div>

      <div className="review-section">
        <label className="review-label">Files (1)</label>

        <div className="review-file-box">
          <div className="review-file-left">
            <div className="review-file-icon">📄</div>
            <div>
              <h5>{selectedFile?.name}</h5>
              <p>1 file</p>
            </div>
          </div>
        </div>
      </div>

      <div className="review-section">
        <label className="review-label">Print Settings</label>

        <div className="review-settings-box">
          <div>
            <span>Paper Size</span>
            <strong>{paperSize.toUpperCase()}</strong>
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

      <div className="review-section">
        <div className="price-summary-box">
          <div className="price-summary-header">Price Summary</div>

          <div className="price-row">
            <span>
              {colorMode === "Black & White" ? "P2/page" : "P5/page"} × {copies} copie(s)
            </span>
            <strong>
              P{" "}
              {(
                (colorMode === "Black & White" ? 2 : 5) *
                Number(copies || 1)
              ).toFixed(2)}
            </strong>
          </div>

          <div className="price-row total">
            <span>Total</span>
            <strong>
              P{" "}
              {(
                (colorMode === "Black & White" ? 2 : 5) *
                Number(copies || 1)
              ).toFixed(2)}
            </strong>
          </div>
        </div>
      </div>
    </section>
  );

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

        <button
          className="new-order-user new-order-user-btn"
          type="button"
          onClick={() => navigate("/profile")}
          title="Profile Settings"
        >
          {user?.profileImageUrl ? (
            <img
              src={user.profileImageUrl}
              alt="Profile"
              className="new-order-user-image"
            />
          ) : (
            initials
          )}
        </button>
      </header>

      <main className="new-order-content">
        <h1>New Print Order</h1>
        <p>Upload your files and configure print settings.</p>

        <div className="new-order-steps">
          <div className="step-item">
            <div className={getStepCircleClass(1)}>{getStepCircleContent(1)}</div>
            <div>
              <strong>Upload Files</strong>
              <span>Add your documents</span>
            </div>
          </div>

          <div className={getStepLineClass(1)} />

          <div className="step-item">
            <div className={getStepCircleClass(2)}>{getStepCircleContent(2)}</div>
            <div>
              <strong>Print Settings</strong>
              <span>Configure options</span>
            </div>
          </div>

          <div className={getStepLineClass(2)} />

          <div className="step-item">
            <div className={getStepCircleClass(3)}>{getStepCircleContent(3)}</div>
            <div>
              <strong>Review & Submit</strong>
              <span>Confirm your order</span>
            </div>
          </div>
        </div>

        {currentStep === 1 && renderStepOne()}
        {currentStep === 2 && renderStepTwo()}
        {currentStep === 3 && renderStepThree()}

        <div className={`new-order-footer ${currentStep === 1 ? "step-one-footer" : ""}`}>
          {currentStep > 1 ? (
            <button className="ghost-btn" onClick={handlePrevious}>
              ← Previous
            </button>
          ) : (
            <div></div>
          )}

          {currentStep < 3 ? (
            <button className="primary-btn submit-order-btn" onClick={handleNext}>
              Next →
            </button>
          ) : (
            <button
              className="primary-btn submit-order-btn"
              onClick={handleSubmit}
              disabled={loading}
              style={{ background: highlightRed }}
            >
              {loading ? "Submitting..." : "👜 Submit Order"}
            </button>
          )}
        </div>
      </main>
    </div>
  );
}

export default NewOrder;