package com.printit.mobile.features.notifications

import android.content.Intent
import android.content.SharedPreferences
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
import com.printit.mobile.features.staff.dashboard.StaffDashboardActivity
import com.printit.mobile.features.student.dashboard.StudentDashboardActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationsMessage: TextView
    private lateinit var notificationsContainer: LinearLayout

    private lateinit var btnBackDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_notifications)

        bindViews()
        setupHeader()
        setupButtons()
        loadNotifications()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvNotificationsMessage = findViewById(R.id.tvNotificationsMessage)
        notificationsContainer = findViewById(R.id.notificationsContainer)
        btnBackDashboard = findViewById(R.id.btnBackDashboard)
    }

    private fun setupHeader() {
        val fullName = sharedPreferences.getString("fullName", "User") ?: "User"
        tvProfileBadge.text = getInitials(fullName)
    }

    private fun setupButtons() {
        btnBackDashboard.setOnClickListener {
            goDashboard()
        }
    }

    private fun loadNotifications() {
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isBlank()) {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show()
            return
        }

        tvNotificationsMessage.text = "Loading notifications..."
        notificationsContainer.removeAllViews()

        RetrofitClient.instance.getNotifications(email)
            .enqueue(object : Callback<List<NotificationResponse>> {
                override fun onResponse(
                    call: Call<List<NotificationResponse>>,
                    response: Response<List<NotificationResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderNotifications(response.body()!!)
                    } else {
                        tvNotificationsMessage.text = "Failed to load notifications."
                    }
                }

                override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                    tvNotificationsMessage.text = "Failed to load notifications."
                    Toast.makeText(
                        this@NotificationsActivity,
                        "Notifications error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderNotifications(notifications: List<NotificationResponse>) {
        notificationsContainer.removeAllViews()

        if (notifications.isEmpty()) {
            tvNotificationsMessage.text = "No notifications yet."
            return
        }

        tvNotificationsMessage.text = "Showing ${notifications.size} notification(s)."

        notifications.forEach { notification ->
            val card: View = layoutInflater.inflate(
                R.layout.item_notification_card,
                notificationsContainer,
                false
            )

            val tvNotificationTitle: TextView = card.findViewById(R.id.tvNotificationTitle)
            val tvNotificationMessage: TextView = card.findViewById(R.id.tvNotificationMessage)
            val tvNotificationStatus: TextView = card.findViewById(R.id.tvNotificationStatus)
            val btnMarkRead: Button = card.findViewById(R.id.btnMarkRead)

            tvNotificationTitle.text = notification.title ?: "Notification"
            tvNotificationMessage.text = notification.message ?: "-"
            tvNotificationStatus.text = if (notification.isRead == true) "Read" else "Unread"

            btnMarkRead.isEnabled = notification.isRead != true
            btnMarkRead.text = if (notification.isRead == true) "Already Read" else "Mark as Read"

            btnMarkRead.setOnClickListener {
                val id = notification.id

                if (id == null) {
                    Toast.makeText(this, "Invalid notification.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                markAsRead(id)
            }

            notificationsContainer.addView(card)
        }
    }

    private fun markAsRead(notificationId: Long) {
        RetrofitClient.instance.markNotificationAsRead(notificationId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@NotificationsActivity,
                            "Notification marked as read.",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadNotifications()
                    } else {
                        Toast.makeText(
                            this@NotificationsActivity,
                            "Failed to mark notification as read.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@NotificationsActivity,
                        "Update error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun goDashboard() {
        val role = sharedPreferences.getString("role", "STUDENT") ?: "STUDENT"

        val intent = when (role.uppercase()) {
            "ADMIN" -> Intent(this, AdminDashboardActivity::class.java)
            "STAFF" -> Intent(this, StaffDashboardActivity::class.java)
            else -> Intent(this, StudentDashboardActivity::class.java)
        }

        startActivity(intent)
        finish()
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }

        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "US"
        }
    }
}