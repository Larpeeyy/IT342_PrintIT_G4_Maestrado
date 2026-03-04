import { useMemo, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthLayout from "../components/AuthLayout";
import { registerUser } from "../services/api";
import "./Auth.css";

function Register() {
  const navigate = useNavigate();

  const [role, setRole] = useState("STUDENT");
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    fullName: "",
    email: "",
    studentId: "",
    staffId: "",
    password: "",
    confirmPassword: "",
  });

  const idLabel = useMemo(() => {
    return role === "STUDENT" ? "Student ID" : "Staff ID";
  }, [role]);

  const idName = useMemo(() => {
    return role === "STUDENT" ? "studentId" : "staffId";
  }, [role]);

  const helperText = useMemo(() => {
    if (role !== "STAFF") return null;
    return (
      <div className="helper">
        Staff accounts require verification against institutional records.
        <br />
        Only registered institutional staff may create staff accounts.
      </div>
    );
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

  return (
    <AuthLayout>
      <div className="auth-card">
        <div className="tabs">
          <Link to="/login">Login</Link>
          <span className="active">Register</span>
        </div>

        <label>Select Account Type</label>

        <div className="role-selector">
          <button
            type="button"
            className={`role-card ${role === "STUDENT" ? "selected" : ""}`}
            onClick={() => setRole("STUDENT")}
          >
            Student
          </button>

          <button
            type="button"
            className={`role-card ${role === "STAFF" ? "selected" : ""}`}
            onClick={() => setRole("STAFF")}
          >
            Staff
          </button>
        </div>

        <label>Full Name</label>
        <input
          type="text"
          name="fullName"
          value={form.fullName}
          onChange={handleChange}
        />

        <label>Email Address</label>
        <input
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
        />

        <label>{idLabel}</label>
        <input
          type="text"
          name={idName}
          value={role === "STUDENT" ? form.studentId : form.staffId}
          onChange={handleChange}
        />

        {helperText}

        <label>Password</label>
        <input
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
        />

        <label>Confirm Password</label>
        <input
          type="password"
          name="confirmPassword"
          value={form.confirmPassword}
          onChange={handleChange}
        />

        <button
          className="primary-btn"
          type="button"
          onClick={handleRegister}
          disabled={loading}
          style={{ opacity: loading ? 0.7 : 1 }}
        >
          {loading ? "Creating..." : "Register"}
        </button>
      </div>
    </AuthLayout>
  );
}

export default Register;