import { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthLayout from "../components/AuthLayout";
import { registerUser } from "../services/api";
import "../components/Auth.css";

function Register() {
  const navigate = useNavigate();

  const [role, setRole] = useState("STUDENT");
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    studentId: "",
    staffId: "",
    department: "",
    password: "",
    confirmPassword: "",
  });

  const idLabel = useMemo(() => {
    return role === "STUDENT" ? "Student ID" : "Staff ID";
  }, [role]);

  const idName = useMemo(() => {
    return role === "STUDENT" ? "studentId" : "staffId";
  }, [role]);

  const accountButtonText = useMemo(() => {
    return role === "STUDENT" ? "Create Student Account" : "Create Staff Account";
  }, [role]);

  const emailHelper = useMemo(() => {
    return role === "STUDENT"
      ? "Use your official student university email address."
      : "Use your official staff university email address.";
  }, [role]);

  const idHelper = useMemo(() => {
    return role === "STUDENT"
      ? "9-digit number on your student card (e.g. 00-0000-000)."
      : "Enter your official staff ID number.";
  }, [role]);

  const handleChange = (e) => {
    setForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const validate = () => {
    if (!form.fullName.trim()) return "Full Name is required.";
    if (!form.email.trim()) return "Email Address is required.";
    if (!form.password) return "Password is required.";
    if (!form.confirmPassword) return "Confirm Password is required.";
    if (form.password !== form.confirmPassword) return "Passwords do not match.";

    if (role === "STUDENT" && !form.studentId.trim()) return "Student ID is required.";
    if (role === "STAFF" && !form.staffId.trim()) return "Staff ID is required.";

    return null;
  };

  const handleRegister = async () => {
    const errMsg = validate();
    if (errMsg) {
      alert(errMsg);
      return;
    }

    const payload = {
      fullName: form.fullName.trim(),
      email: form.email.trim(),
      password: form.password,
      role,
      studentId: role === "STUDENT" ? form.studentId.trim() : null,
      staffId: role === "STAFF" ? form.staffId.trim() : null,
    };

    try {
      setLoading(true);
      const res = await registerUser(payload);

      if (res?.data?.id) {
        alert("Registration successful. You can now login.");
        navigate("/login");
        return;
      }

      alert("Registration done, but no user returned. Check backend response.");
    } catch (e) {
      const msg =
        e?.response?.data?.message ||
        e?.response?.data ||
        "Registration failed. Check backend and console.";
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
    >
      <div className="auth-form-shell">
        <p className="auth-shell-kicker">UNIVERSITY PRINT SERVICES</p>
        <h1 className="auth-shell-title">Create your account</h1>
        <p className="auth-shell-subtitle">
          Join PrintIT to start submitting print jobs on campus.
        </p>

        <div className="auth-top-switch">
          <Link to="/login" className="auth-top-switch-item">
            Sign In
          </Link>
          <span className="auth-top-switch-item active">Register</span>
        </div>

        <div className="auth-panel">
          <div className="auth-field-group">
            <label>I am a...</label>

            <div className="role-selector modern-role-selector">
              <button
                type="button"
                className={`role-card modern-role-card ${
                  role === "STUDENT" ? "selected" : ""
                }`}
                onClick={() => setRole("STUDENT")}
              >
                <span className="role-left">
                  <span className="role-icon">🎓</span>
                  Student
                </span>
                {role === "STUDENT" && <span className="role-dot"></span>}
              </button>

              <button
                type="button"
                className={`role-card modern-role-card ${
                  role === "STAFF" ? "selected" : ""
                }`}
                onClick={() => setRole("STAFF")}
              >
                <span className="role-left">
                  <span className="role-icon">💼</span>
                  Staff
                </span>
                {role === "STAFF" && <span className="role-dot"></span>}
              </button>
            </div>
          </div>

          <div className="auth-field-group">
            <label>Full Name</label>
            <div className="input-icon-wrap">
              <span className="input-icon">👤</span>
              <input
                type="text"
                name="fullName"
                placeholder={
                  role === "STUDENT"
                    ? "Enter your full name"
                    : "Enter your full name"
                }
                value={form.fullName}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="auth-field-group">
            <label>University Email</label>
            <p className="field-helper">{emailHelper}</p>
            <div className="input-icon-wrap">
              <span className="input-icon">✉</span>
              <input
                type="email"
                name="email"
                placeholder={
                  role === "STUDENT"
                    ? "firstname.lastname@cit.edu"
                    : "you@university.edu"
                }
                value={form.email}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="auth-field-group">
            <label>{idLabel}</label>
            <p className="field-helper">{idHelper}</p>
            <div className="input-icon-wrap">
              <span className="input-icon">#</span>
              <input
                type="text"
                name={idName}
                placeholder={role === "STUDENT" ? "00-0000-000" : "00-0000-000"}
                value={role === "STUDENT" ? form.studentId : form.staffId}
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="auth-field-group">
            <label>Faculty / Department</label>
            <div className="select-wrap">
              <select
                name="department"
                value={form.department}
                onChange={handleChange}
              >
                <option value="">Select department...</option>
                <option value="College of Computer Studies">
                  College of Computer Studies
                </option>
                <option value="Engineering">Engineering</option>
                <option value="Business">Business</option>
                <option value="Arts and Sciences">Arts and Sciences</option>
                <option value="Administration">Administration</option>
              </select>
              <span className="select-arrow">⌄</span>
            </div>
          </div>

          <div className="auth-field-group">
            <label>Password</label>
            <div className="input-icon-wrap password-field modern">
              <span className="input-icon">🔒</span>
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                placeholder="Min. 8 characters"
                value={form.password}
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

          <div className="auth-field-group">
            <label>Confirm Password</label>
            <div className="input-icon-wrap password-field modern">
              <span className="input-icon">🔒</span>
              <input
                type={showConfirmPassword ? "text" : "password"}
                name="confirmPassword"
                placeholder="Re-enter your password"
                value={form.confirmPassword}
                onChange={handleChange}
              />
              <button
                type="button"
                className="eye-btn"
                onClick={() => setShowConfirmPassword((prev) => !prev)}
                aria-label="Toggle password"
              >
                {showConfirmPassword ? "🙈" : "👁"}
              </button>
            </div>
          </div>

          <button
            className="primary-btn modern-primary"
            type="button"
            onClick={handleRegister}
            disabled={loading}
            style={{ opacity: loading ? 0.8 : 1 }}
          >
            {loading ? "Creating..." : accountButtonText}
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
      </div>
    </AuthLayout>
  );
}

export default Register;