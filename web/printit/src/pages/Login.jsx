import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthLayout from "../components/AuthLayout";
import { loginUser } from "../services/api";
import "../components/Auth.css";

function Login() {
  const navigate = useNavigate();

  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);

  const [login, setLogin] = useState({
    email: "",
    password: "",
  });

  const redirectByRole = useCallback(
    (role) => {
      if (role === "ADMIN") {
        navigate("/admin/dashboard");
      } else if (role === "STAFF") {
        navigate("/staff/dashboard");
      } else {
        navigate("/student/home");
      }
    },
    [navigate]
  );

  useEffect(() => {
    const savedUser = localStorage.getItem("printit_user");

    if (savedUser) {
      try {
        const parsedUser = JSON.parse(savedUser);
        redirectByRole(parsedUser.role);
      } catch (error) {
        console.error("Failed to parse saved user:", error);
        localStorage.removeItem("printit_user");
      }
    }
  }, [redirectByRole]);

  const handleChange = (e) => {
    setLogin((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleLogin = async () => {
    if (!login.email.trim() || !login.password) {
      alert("Email and Password are required.");
      return;
    }

    try {
      setLoading(true);

      const res = await loginUser({
        email: login.email.trim(),
        password: login.password,
      });

      if (!res.data) {
        alert("Invalid credentials.");
        return;
      }

      localStorage.setItem("printit_user", JSON.stringify(res.data));

      alert("Login successful!");
      redirectByRole(res.data.role);
    } catch (e) {
      const msg =
        e?.response?.data?.message ||
        e?.response?.data ||
        "Login failed. Check backend and console.";
      console.error(e);
      alert(msg);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleAuth = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };


  return (
    <AuthLayout
      bottomMode="register"
      bottomText="New to PrintIT?"
      bottomLinkText="Create an account"
      bottomLinkTo="/register"
    >
      <div className="auth-form-shell">
        <p className="auth-shell-kicker">UNIVERSITY PRINT SERVICES</p>
        <h1 className="auth-shell-title">Welcome back</h1>
        <p className="auth-shell-subtitle">
          Sign in to manage your print jobs and orders.
        </p>

        <div className="auth-top-switch">
          <span className="auth-top-switch-item active">Sign In</span>
          <Link to="/register" className="auth-top-switch-item">
            Register
          </Link>
        </div>

        <div className="auth-panel">
          <div className="auth-field-group">
            <label>University Email</label>
            <div className="input-icon-wrap">
              <span className="input-icon">✉</span>
              <input
                type="email"
                name="email"
                placeholder="you@university.edu"
                value={login.email}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="auth-field-group">
            <div className="label-row">
              <label>Password</label>
              <span className="auth-link-text">Forgot password?</span>
            </div>

            <div className="input-icon-wrap password-field modern">
              <span className="input-icon">🔒</span>
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="Enter your password"
                value={login.password}
                onChange={handleChange}
              />
              <button
                type="button"
                className="eye-btn"
                onClick={() => setShowPassword((prev) => !prev)}
                aria-label="Toggle password"
              >
                {showPassword ? "🙈" : "👁"}
              </button>
            </div>
          </div>

          <div className="auth-row modern-row">
            <label className="remember modern-remember">
              <input type="checkbox" />
              Keep me signed in for 30 days
            </label>
          </div>

          <button
            className="primary-btn modern-primary"
            type="button"
            onClick={handleLogin}
            disabled={loading}
            style={{ opacity: loading ? 0.8 : 1 }}
          >
            {loading ? "Signing in..." : "Sign In to PrintIT"}
          </button>

          <div className="divider modern-divider">
            <span></span>
            <div>OR</div>
            <span></span>
          </div>

          <button
            className="google-btn modern-google-btn"
            type="button"
            onClick={handleGoogleAuth}
          >
            <span className="google-mark google-colored">G</span>
            Continue with Google
          </button>

        </div>

        <p className="auth-bottom-inline">
          Don&apos;t have an account?{" "}
          <Link to="/register">Register here</Link>
        </p>
      </div>
    </AuthLayout>
  );
}

export default Login;