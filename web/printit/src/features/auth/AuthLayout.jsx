import "./Auth.css";

function AuthLayout({
  children
}) {
  return (
    <div className="auth-page">
      <div className="auth-left-panel">
        <div className="auth-left-overlay"></div>
        <div className="auth-left-grid"></div>

        <div className="floating-paper paper-one"></div>
        <div className="floating-paper paper-two"></div>
        <div className="floating-paper paper-three"></div>
        <div className="floating-paper paper-four"></div>

        <div className="auth-left-content">
          <div className="brand-icon-box"></div>

          <h1 className="brand-title">
            <span className="brand-white">Print</span>
            <span className="brand-gold">IT</span>
          </h1>

          <p className="brand-subtitle">PRINT SMARTER. ORDER FASTER.</p>

          <div className="brand-divider">
            <span></span>
            <span className="gold-line"></span>
            <span></span>
          </div>

          <div className="brand-features">
            <div className="brand-feature">
              <div className="feature-icon"></div>
              <div>
                <h3>Instant Print Orders</h3>
                <p>Upload, configure, and submit in under 60 seconds.</p>
              </div>
            </div>

            <div className="brand-feature">
              <div className="feature-icon"></div>
              <div>
                <h3>Track Every Job</h3>
                <p>Real-time status from queue to collection point.</p>
              </div>
            </div>

            <div className="brand-feature">
              <div className="feature-icon"></div>
              <div>
                <h3>Built for Campus</h3>
                <p>Students and staff unified on one platform.</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="auth-right-panel">
        {children}
      </div>
    </div>
  );
}

export default AuthLayout;