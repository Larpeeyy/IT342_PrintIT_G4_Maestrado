import { useEffect, useMemo, useRef, useState } from "react";
import { useNavigate } from "react-router-dom";
import { changePassword, getProfile, updateProfile } from "../services/api";
import { supabase } from "../services/supabaseClient";
import "./ProfileSettings.css";

function ProfileSettings() {
  const navigate = useNavigate();
  const fileInputRef = useRef(null);

  const storedUser = useMemo(() => {
    try {
      return JSON.parse(localStorage.getItem("printit_user")) || {};
    } catch {
      return {};
    }
  }, []);

  const [profile, setProfile] = useState({
    id: null,
    fullName: "",
    username: "",
    email: "",
    role: "",
    studentId: "",
    staffId: "",
    profileImageUrl: "",
  });

  const [initialProfile, setInitialProfile] = useState(null);

  const [passwordForm, setPasswordForm] = useState({
    currentPassword: "",
    newPassword: "",
    confirmNewPassword: "",
  });

  const [loadingProfile, setLoadingProfile] = useState(true);
  const [savingProfile, setSavingProfile] = useState(false);
  const [savingPassword, setSavingPassword] = useState(false);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const [imageLoadError, setImageLoadError] = useState(false);

  const [showCurrentPassword, setShowCurrentPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const initials = useMemo(() => {
    if (!profile.fullName) return "PI";
    return profile.fullName
      .split(" ")
      .map((part) => part[0])
      .join("")
      .slice(0, 2)
      .toUpperCase();
  }, [profile.fullName]);

  const roleLabel = useMemo(() => {
    if (profile.role === "ADMIN") return "Admin";
    if (profile.role === "STAFF") return "Staff";
    return "Student";
  }, [profile.role]);

  const roleDetailLabel = useMemo(() => {
    if (profile.role === "STAFF") return "STAFF ID";
    if (profile.role === "ADMIN") return "ACCESS";
    return "STUDENT ID";
  }, [profile.role]);

  const roleDetailValue = useMemo(() => {
    if (profile.role === "STAFF") return profile.staffId || "Not set";
    if (profile.role === "ADMIN") return "Administrative access";
    return profile.studentId || "Not set";
  }, [profile.role, profile.staffId, profile.studentId]);

  useEffect(() => {
    setImageLoadError(false);
  }, [profile.profileImageUrl]);

  useEffect(() => {
    const loadProfile = async () => {
      if (!storedUser?.email) {
        navigate("/login");
        return;
      }

      try {
        setLoadingProfile(true);
        const res = await getProfile(storedUser.email);

        const nextProfile = {
          id: res.data?.id ?? null,
          fullName: res.data?.fullName ?? "",
          username: res.data?.username ?? storedUser.email?.split("@")[0] ?? "",
          email: res.data?.email ?? storedUser.email ?? "",
          role: res.data?.role ?? storedUser.role ?? "",
          studentId: res.data?.studentId ?? "",
          staffId: res.data?.staffId ?? "",
          profileImageUrl: res.data?.profileImageUrl ?? "",
        };

        setProfile(nextProfile);
        setInitialProfile(nextProfile);

        localStorage.setItem(
          "printit_user",
          JSON.stringify({
            ...storedUser,
            ...nextProfile,
          })
        );
      } catch (error) {
        console.error("Failed to load profile:", error);
        alert("Failed to load profile.");
      } finally {
        setLoadingProfile(false);
      }
    };

    loadProfile();
  }, [navigate, storedUser]);

  const handleProfileChange = (e) => {
    const { name, value } = e.target;
    setProfile((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleChoosePhoto = () => {
    fileInputRef.current?.click();
  };

  const handlePhotoUpload = async (e) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const allowedTypes = ["image/jpeg", "image/png", "image/webp"];
    if (!allowedTypes.includes(file.type)) {
      alert("Only JPG, PNG, and WEBP images are allowed.");
      return;
    }

    if (file.size > 2 * 1024 * 1024) {
      alert("Image must be 2 MB or below.");
      return;
    }

    try {
      setUploadingPhoto(true);

      const safeName = file.name.replace(/\s+/g, "-").toLowerCase();
      const folderName = (profile.email || "user").replace(/[^a-zA-Z0-9._-]/g, "_");
      const filePath = `${folderName}/${Date.now()}-${safeName}`;

      const { data: uploadData, error: uploadError } = await supabase.storage
        .from("profile-photos")
        .upload(filePath, file, {
          upsert: true,
        });

      if (uploadError) {
        throw uploadError;
      }

      const { data: publicUrlData } = supabase.storage
        .from("profile-photos")
        .getPublicUrl(uploadData.path);

      const finalUrl = `${publicUrlData.publicUrl}?t=${Date.now()}`;

      const res = await updateProfile({
        email: profile.email,
        fullName: profile.fullName.trim(),
        username: profile.username.trim(),
        profileImageUrl: finalUrl,
      });

      const updatedProfile = {
        ...profile,
        ...res.data,
        profileImageUrl: finalUrl,
      };

      setImageLoadError(false);
      setProfile(updatedProfile);
      setInitialProfile(updatedProfile);

      localStorage.setItem(
        "printit_user",
        JSON.stringify({
          ...storedUser,
          ...updatedProfile,
          profileImageUrl: finalUrl,
        })
      );

      alert("Photo uploaded successfully.");
    } catch (error) {
      console.error("Photo upload failed:", error);
      alert(error?.message || "Failed to upload photo.");
    } finally {
      setUploadingPhoto(false);
      e.target.value = "";
    }
  };

  const handleSaveProfile = async () => {
    if (!profile.fullName.trim()) {
      alert("Full name is required.");
      return;
    }

    if (!profile.username.trim()) {
      alert("Username is required.");
      return;
    }

    try {
      setSavingProfile(true);

      const res = await updateProfile({
        email: profile.email,
        fullName: profile.fullName.trim(),
        username: profile.username.trim(),
        profileImageUrl: profile.profileImageUrl || "",
      });

      const updatedProfile = {
        ...profile,
        ...res.data,
      };

      setProfile(updatedProfile);
      setInitialProfile(updatedProfile);

      localStorage.setItem(
        "printit_user",
        JSON.stringify({
          ...storedUser,
          ...updatedProfile,
        })
      );

      alert("Profile updated successfully.");
    } catch (error) {
      console.error("Profile update failed:", error);
      const message =
        error?.response?.data?.message ||
        error?.response?.data ||
        "Failed to update profile.";
      alert(message);
    } finally {
      setSavingProfile(false);
    }
  };

  const handleCancelProfileChanges = () => {
    if (initialProfile) {
      setProfile(initialProfile);
      setImageLoadError(false);
    }
  };

  const handleChangePassword = async () => {
    if (!passwordForm.currentPassword) {
      alert("Current password is required.");
      return;
    }

    if (!passwordForm.newPassword || passwordForm.newPassword.length < 8) {
      alert("New password must be at least 8 characters.");
      return;
    }

    if (passwordForm.newPassword !== passwordForm.confirmNewPassword) {
      alert("New passwords do not match.");
      return;
    }

    try {
      setSavingPassword(true);

      await changePassword({
        email: profile.email,
        currentPassword: passwordForm.currentPassword,
        newPassword: passwordForm.newPassword,
      });

      setPasswordForm({
        currentPassword: "",
        newPassword: "",
        confirmNewPassword: "",
      });

      alert("Password updated successfully.");
    } catch (error) {
      console.error("Password update failed:", error);
      const message =
        error?.response?.data?.message ||
        error?.response?.data ||
        "Failed to update password.";
      alert(message);
    } finally {
      setSavingPassword(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("printit_user");
    localStorage.removeItem("token");
    localStorage.removeItem("authToken");
    localStorage.removeItem("studentId");
    navigate("/login");
  };

  const goDashboard = () => {
    if (profile.role === "ADMIN") navigate("/admin/dashboard");
    else if (profile.role === "STAFF") navigate("/staff/dashboard");
    else navigate("/student/home");
  };

  if (loadingProfile) {
    return (
      <div className="profile-settings-page">
        <div className="profile-settings-shell">
          <p>Loading profile...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-settings-page">
      <header className="profile-top-navbar">
        <div className="profile-top-brand" onClick={goDashboard}>
          <div className="profile-top-logo"></div>
          <div className="profile-top-brand-text">PrintIT</div>
        </div>

        <nav className="profile-top-nav">
          <button onClick={goDashboard}>Dashboard</button>

          {profile.role === "STUDENT" && (
            <>
              <button onClick={() => navigate("/student/new-order")}>+ New Order</button>
              <button onClick={() => navigate("/student/orders")}>Orders</button>
              <button onClick={() => navigate("/student/payments")}>Payments</button>
            </>
          )}

          {profile.role !== "STUDENT" && (
            <button className="active" onClick={() => navigate("/profile")}>
              Profile Settings
            </button>
          )}
        </nav>

        <div className="profile-top-actions">
          <button
            className="profile-top-avatar"
            type="button"
            onClick={() => navigate("/profile")}
            title="Profile Settings"
          >
            {profile.profileImageUrl && !imageLoadError ? (
              <img
                src={profile.profileImageUrl}
                alt="Profile"
                className="profile-top-avatar-image"
                onError={() => setImageLoadError(true)}
              />
            ) : (
              initials
            )}
          </button>
        </div>
      </header>

      <div className="profile-settings-shell">
        <div className="profile-settings-topbar">
          <div>
            <h1>Profile Settings</h1>
            <p>Manage your account information and security preferences.</p>
          </div>
        </div>

        <section className="profile-card">
          <div className="profile-card-header">
            <div className="profile-section-icon">👤</div>
            <div>
              <h2>Profile Information</h2>
              <p>Update your personal details</p>
            </div>
          </div>

          <div className="profile-card-body compact-layout">
            <div className="profile-photo-column">
              <div className="profile-photo-box">
                {profile.profileImageUrl && !imageLoadError ? (
                  <img
                    src={profile.profileImageUrl}
                    alt="Profile"
                    className="profile-photo-image"
                    onError={() => setImageLoadError(true)}
                  />
                ) : (
                  <span>{initials}</span>
                )}
              </div>

              <input
                ref={fileInputRef}
                type="file"
                accept="image/png,image/jpeg,image/webp"
                hidden
                onChange={handlePhotoUpload}
              />

              <button
                type="button"
                className="photo-upload-btn"
                onClick={handleChoosePhoto}
                disabled={uploadingPhoto}
              >
                {uploadingPhoto ? "Uploading..." : "📷 Change Photo"}
              </button>

              <small>JPG or PNG, max 2 MB</small>
            </div>

            <div className="profile-form-column">
              <div className="profile-grid two-columns profile-top-grid">
                <div className="profile-field">
                  <label>FULL NAME</label>
                  <input
                    type="text"
                    name="fullName"
                    value={profile.fullName}
                    onChange={handleProfileChange}
                  />
                </div>

                <div className="profile-field">
                  <label>USERNAME</label>
                  <input
                    type="text"
                    name="username"
                    value={profile.username}
                    onChange={handleProfileChange}
                  />
                </div>
              </div>

              <div className="profile-field profile-email-field">
                <label>EMAIL ADDRESS</label>
                <input type="email" value={profile.email} disabled />
                <small>Email changes require administrator approval.</small>
              </div>

              <div className="profile-grid two-columns">
                <div className="profile-field">
                  <label>ROLE</label>
                  <div className="readonly-role-box">
                    <span className={`role-pill role-${profile.role?.toLowerCase()}`}>
                      {roleLabel}
                    </span>
                    <strong>ASSIGNED</strong>
                  </div>
                </div>

                <div className="profile-field">
                  <label>{roleDetailLabel}</label>
                  <input type="text" value={roleDetailValue} disabled />
                </div>
              </div>

              <div className="profile-actions">
                <button
                  type="button"
                  className="cancel-btn"
                  onClick={handleCancelProfileChanges}
                >
                  Cancel
                </button>

                <button
                  type="button"
                  className="save-btn"
                  onClick={handleSaveProfile}
                  disabled={savingProfile}
                >
                  {savingProfile ? "Saving..." : "💾 Save Changes"}
                </button>
              </div>
            </div>
          </div>
        </section>

        <section className="profile-card">
          <div className="profile-card-header">
            <div className="profile-section-icon">🔒</div>
            <div>
              <h2>Change Password</h2>
              <p>Keep your account secure with a strong password</p>
            </div>
          </div>

          <div className="profile-card-body password-compact">
            <div className="profile-grid password-grid">
              <div className="profile-field">
                <label>CURRENT PASSWORD</label>
                <div className="password-input-wrap">
                  <input
                    type={showCurrentPassword ? "text" : "password"}
                    name="currentPassword"
                    value={passwordForm.currentPassword}
                    onChange={handlePasswordChange}
                    placeholder="Enter current password"
                  />
                  <button
                    type="button"
                    className="eye-toggle-btn"
                    onClick={() => setShowCurrentPassword((prev) => !prev)}
                  >
                    {showCurrentPassword ? "🙈" : "👁"}
                  </button>
                </div>
              </div>

              <div className="profile-field">
                <label>NEW PASSWORD</label>
                <div className="password-input-wrap">
                  <input
                    type={showNewPassword ? "text" : "password"}
                    name="newPassword"
                    value={passwordForm.newPassword}
                    onChange={handlePasswordChange}
                    placeholder="Enter new password"
                  />
                  <button
                    type="button"
                    className="eye-toggle-btn"
                    onClick={() => setShowNewPassword((prev) => !prev)}
                  >
                    {showNewPassword ? "🙈" : "👁"}
                  </button>
                </div>
              </div>

              <div className="profile-field">
                <label>CONFIRM NEW PASSWORD</label>
                <div className="password-input-wrap">
                  <input
                    type={showConfirmPassword ? "text" : "password"}
                    name="confirmNewPassword"
                    value={passwordForm.confirmNewPassword}
                    onChange={handlePasswordChange}
                    placeholder="Re-enter new password"
                  />
                  <button
                    type="button"
                    className="eye-toggle-btn"
                    onClick={() => setShowConfirmPassword((prev) => !prev)}
                  >
                    {showConfirmPassword ? "🙈" : "👁"}
                  </button>
                </div>
              </div>
            </div>

            <small className="password-note">
              Use at least 8 characters with a mix of letters, numbers, and symbols.
            </small>

            <div className="password-actions">
              <button
                type="button"
                className="save-btn"
                onClick={handleChangePassword}
                disabled={savingPassword}
              >
                {savingPassword ? "Updating..." : "🔒 Update Password"}
              </button>

              <button
                type="button"
                className="cancel-btn"
                onClick={handleLogout}
              >
                Logout
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}

export default ProfileSettings;