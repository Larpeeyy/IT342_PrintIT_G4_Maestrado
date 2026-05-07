import { useEffect, useState } from "react";
import AdminTopbar from "../../components/AdminTopbar";
import { getAdminUsers } from "../../services/api";
import "./AdminUsers.css";

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadUsers = async () => {
      try {
        setLoading(true);
        const res = await getAdminUsers();
        setUsers(res.data || []);
      } catch (error) {
        console.error("Failed to load users:", error);
        alert(
          error?.response?.data?.message ||
            error?.message ||
            "Failed to load users."
        );
      } finally {
        setLoading(false);
      }
    };

    loadUsers();
  }, []);

  const isRole = (value) => {
    return ["ADMIN", "STAFF", "STUDENT"].includes(String(value || "").toUpperCase());
  };

  const isApprovalStatus = (value) => {
    return ["APPROVED", "PENDING", "REJECTED"].includes(
      String(value || "").toUpperCase()
    );
  };

  const getCorrectRole = (user) => {
    if (isRole(user.role)) return user.role;
    if (isRole(user.username)) return user.username;
    if (isRole(user.approvalStatus)) return user.approvalStatus;
    return "-";
  };

  const getCorrectApprovalStatus = (user) => {
    if (isApprovalStatus(user.approvalStatus)) return user.approvalStatus;
    if (isApprovalStatus(user.role)) return user.role;
    if (isApprovalStatus(user.username)) return user.username;
    return "-";
  };

  const getCorrectUsername = (user) => {
    const possibleUsername = user.username;

    if (isRole(possibleUsername) || isApprovalStatus(possibleUsername)) {
      if (
        user.approvalStatus &&
        !isRole(user.approvalStatus) &&
        !isApprovalStatus(user.approvalStatus)
      ) {
        return user.approvalStatus;
      }

      return "-";
    }

    return possibleUsername || "-";
  };

  const getApprovalClass = (status) => {
    const value = String(status || "").toUpperCase();

    if (value === "APPROVED") return "admin-users-status-green";
    if (value === "PENDING") return "admin-users-status-yellow";
    if (value === "REJECTED") return "admin-users-status-red";

    return "admin-users-status-gray";
  };

  const filteredUsers = users.filter((user) => {
    const username = getCorrectUsername(user);
    const role = getCorrectRole(user);
    const approvalStatus = getCorrectApprovalStatus(user);

    const target = `${user.fullName || ""} ${user.email || ""} ${username} ${role} ${approvalStatus}`.toLowerCase();

    return target.includes(search.toLowerCase());
  });

  return (
    <div className="admin-page">
      <AdminTopbar activeTab="users" />

      <main className="admin-users-content">
        <section className="admin-users-heading">
          <h1>User Management</h1>
          <p>Manage all platform users and permissions.</p>
        </section>

        <section className="admin-users-toolbar">
          <input
            type="text"
            placeholder="Search users..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="admin-users-card">
          {loading ? (
            <p className="admin-users-empty-text">Loading users...</p>
          ) : filteredUsers.length === 0 ? (
            <p className="admin-users-empty-text">No users found.</p>
          ) : (
            <table className="admin-users-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Username</th>
                  <th>Role</th>
                  <th>Approval Status</th>
                </tr>
              </thead>

              <tbody>
                {filteredUsers.map((user) => {
                  const username = getCorrectUsername(user);
                  const role = getCorrectRole(user);
                  const approvalStatus = getCorrectApprovalStatus(user);

                  return (
                    <tr key={user.id}>
                      <td>{user.fullName || "-"}</td>
                      <td>{user.email || "-"}</td>
                      <td>{username}</td>
                      <td>{role}</td>
                      <td>
                        <span
                          className={`admin-users-status-pill ${getApprovalClass(
                            approvalStatus
                          )}`}
                        >
                          {approvalStatus}
                        </span>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          )}
        </section>
      </main>
    </div>
  );
}

export default AdminUsers;