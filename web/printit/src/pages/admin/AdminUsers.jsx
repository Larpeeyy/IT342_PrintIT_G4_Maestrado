import { useEffect, useState } from "react";
import AdminTopbar from "../../components/AdminTopbar";
import { getAdminUsers } from "../../services/api";
import "./AdminUsers.css";

function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const loadUsers = async () => {
      try {
        const res = await getAdminUsers();
        setUsers(res.data || []);
      } catch (error) {
        console.error("Failed to load users:", error);
        alert(error?.response?.data?.message || error?.message || "Failed to load users.");
      }
    };

    loadUsers();
  }, []);

  const filteredUsers = users.filter((user) => {
    const target =
      `${user.fullName || ""} ${user.email || ""} ${user.role || ""} ${user.username || ""}`.toLowerCase();
    return target.includes(search.toLowerCase());
  });

  return (
    <div className="admin-page">
      <AdminTopbar activeTab="users" />

      <main className="admin-users-content">
        <h1>User Management</h1>
        <p>Manage all platform users and permissions.</p>

        <section className="admin-users-toolbar">
          <input
            type="text"
            placeholder="Search users..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </section>

        <section className="admin-users-table-card">
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
              {filteredUsers.map((user) => (
                <tr key={user.id}>
                  <td>{user.fullName}</td>
                  <td>{user.email}</td>
                  <td>{user.username || "-"}</td>
                  <td>{user.role}</td>
                  <td>{user.approvalStatus || "-"}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>
      </main>
    </div>
  );
}

export default AdminUsers;