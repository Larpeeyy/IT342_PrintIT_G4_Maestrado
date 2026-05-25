package com.printit.mobile.features.admin.users

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.admin.dashboard.AdminDashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminUsersActivity : AppCompatActivity() {

    private lateinit var tvUsersMessage: TextView
    private lateinit var usersContainer: LinearLayout
    private lateinit var btnDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_users)

        tvUsersMessage = findViewById(R.id.tvUsersMessage)
        usersContainer = findViewById(R.id.usersContainer)
        btnDashboard = findViewById(R.id.btnDashboard)

        btnDashboard.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        loadUsers()
    }

    private fun loadUsers() {
        tvUsersMessage.text = "Loading users..."
        usersContainer.removeAllViews()

        RetrofitClient.instance.getAdminUsers()
            .enqueue(object : Callback<List<AdminUserResponse>> {
                override fun onResponse(
                    call: Call<List<AdminUserResponse>>,
                    response: Response<List<AdminUserResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderUsers(response.body()!!)
                    } else {
                        tvUsersMessage.text = "Failed to load users."
                    }
                }

                override fun onFailure(call: Call<List<AdminUserResponse>>, t: Throwable) {
                    tvUsersMessage.text = "Failed to load users."
                    Toast.makeText(
                        this@AdminUsersActivity,
                        "Users error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderUsers(users: List<AdminUserResponse>) {
        usersContainer.removeAllViews()

        if (users.isEmpty()) {
            tvUsersMessage.text = "No users found."
            return
        }

        tvUsersMessage.text = "Showing ${users.size} user(s)."

        users.forEach { user ->
            val card: View = layoutInflater.inflate(
                R.layout.item_admin_user_card,
                usersContainer,
                false
            )

            val tvUserName: TextView = card.findViewById(R.id.tvUserName)
            val tvUserEmail: TextView = card.findViewById(R.id.tvUserEmail)
            val tvUserDetails: TextView = card.findViewById(R.id.tvUserDetails)
            val tvUserStatus: TextView = card.findViewById(R.id.tvUserStatus)

            tvUserName.text = user.fullName ?: "Unnamed User"
            tvUserEmail.text = user.email ?: "-"
            tvUserDetails.text = "${user.username ?: "No username"} • ${user.role ?: "No role"}"
            tvUserStatus.text = user.approvalStatus ?: "N/A"

            usersContainer.addView(card)
        }
    }
}