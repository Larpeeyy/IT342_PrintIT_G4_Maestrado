import { useCallback, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthLayout from "../components/AuthLayout";
import { loginUser } from "../services/api";
import "./Auth.css";

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
    <AuthLayout>
      <div className="auth-card">
        <div className="tabs">
          <span className="active">Login</span>
          <Link to="/register">Register</Link>
        </div>

        <label>Email Address</label>
        <input
          type="email"
          name="email"
          value={login.email}
          onChange={handleChange}
        />

        <label>Password</label>
        <div className="password-field">
          <input
            type={showPassword ? "text" : "password"}
            name="password"
            value={login.password}
            onChange={handleChange}
          />
          <button
            type="button"
            className="eye-btn"
            onClick={() => setShowPassword((prev) => !prev)}
            aria-label="Toggle password"
          >
            👁
          </button>
        </div>

        <div className="auth-row">
          <label className="remember">
            <input type="checkbox" />
            Remember me
          </label>

          <span style={{ color: "#8C2F39", cursor: "pointer", fontWeight: 500 }}>
            Forgot Password?
          </span>
        </div>

        <button
          className="primary-btn"
          type="button"
          onClick={handleLogin}
          disabled={loading}
          style={{ opacity: loading ? 0.7 : 1 }}
        >
          {loading ? "Logging in..." : "Login"}
        </button>

        <div className="divider">
          <span></span>
          <div>or</div>
          <span></span>
        </div>

        <button className="google-btn" type="button" onClick={handleGoogleAuth}>
          <span style={{ fontSize: 16 }}>G</span>
          Continue with Google
        </button>

        <p className="note">Your dashboard will be determined by your account role.</p>
      </div>
    </AuthLayout>
  );
}

export default Login;