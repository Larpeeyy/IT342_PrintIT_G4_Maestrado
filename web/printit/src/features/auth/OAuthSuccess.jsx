import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";

function OAuthSuccess() {
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(location.search);

    const user = {
      email: params.get("email") || "",
      fullName: params.get("fullName") || "",
      role: params.get("role") || "STUDENT",
      provider: "google",
    };

    if (!user.email) {
      alert("Google login failed. No user data returned.");
      navigate("/login");
      return;
    }

    localStorage.setItem("printit_user", JSON.stringify(user));

    alert("Google login successful!");

    // Temporary destination for now
    navigate("/login");
  }, [location, navigate]);

  return (
    <div style={{ padding: "40px", textAlign: "center" }}>
      Signing you in with Google...
    </div>
  );
}

export default OAuthSuccess;