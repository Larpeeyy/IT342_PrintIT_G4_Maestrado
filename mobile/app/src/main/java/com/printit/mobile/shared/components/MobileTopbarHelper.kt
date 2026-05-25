package com.printit.mobile.shared.components

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.notifications.NotificationResponse
import com.printit.mobile.features.notifications.UnreadNotificationCountResponse
import com.printit.mobile.features.profile.ProfileSettingsActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MobileTopbarHelper {

    fun setup(
        activity: AppCompatActivity,
        profileBadge: TextView,
        notificationBell: TextView
    ) {
        val prefs = activity.getSharedPreferences("printit_prefs", Context.MODE_PRIVATE)
        val fullName = prefs.getString("fullName", "User") ?: "User"

        profileBadge.text = getInitials(fullName)

        profileBadge.setOnClickListener {
            activity.startActivity(Intent(activity, ProfileSettingsActivity::class.java))
        }

        notificationBell.text = "🔔"

        notificationBell.setOnClickListener {
            showNotificationsDropdown(activity, prefs, notificationBell)
        }

        loadUnreadCount(notificationBell, prefs)
    }

    private fun loadUnreadCount(notificationBell: TextView, prefs: SharedPreferences) {
        val email = prefs.getString("email", "") ?: ""

        if (email.isBlank()) {
            notificationBell.text = "🔔"
            return
        }

        RetrofitClient.instance.getUnreadNotificationCount(email)
            .enqueue(object : Callback<UnreadNotificationCountResponse> {
                override fun onResponse(
                    call: Call<UnreadNotificationCountResponse>,
                    response: Response<UnreadNotificationCountResponse>
                ) {
                    val count = response.body()?.count ?: 0L
                    notificationBell.text = if (count > 0) "🔔 $count" else "🔔"
                }

                override fun onFailure(
                    call: Call<UnreadNotificationCountResponse>,
                    t: Throwable
                ) {
                    notificationBell.text = "🔔"
                }
            })
    }

    private fun showNotificationsDropdown(
        activity: AppCompatActivity,
        prefs: SharedPreferences,
        anchor: View
    ) {
        val popupView = LayoutInflater.from(activity)
            .inflate(R.layout.layout_notification_dropdown, null)

        val messageText = popupView.findViewById<TextView>(R.id.tvNotificationDropdownMessage)
        val container = popupView.findViewById<LinearLayout>(R.id.notificationDropdownContainer)

        val popupWindow = PopupWindow(
            popupView,
            dpToPx(activity, 330),
            dpToPx(activity, 430),
            true
        )

        popupWindow.elevation = dpToPx(activity, 10).toFloat()
        popupWindow.showAsDropDown(anchor, -dpToPx(activity, 290), dpToPx(activity, 8))

        val email = prefs.getString("email", "") ?: ""

        if (email.isBlank()) {
            messageText.text = "No logged-in user found."
            return
        }

        messageText.text = "Loading notifications..."
        container.removeAllViews()

        RetrofitClient.instance.getNotifications(email)
            .enqueue(object : Callback<List<NotificationResponse>> {
                override fun onResponse(
                    call: Call<List<NotificationResponse>>,
                    response: Response<List<NotificationResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val notifications = response.body().orEmpty()

                        if (notifications.isEmpty()) {
                            messageText.text = "No notifications yet."
                            return
                        }

                        messageText.text = "Latest updates"
                        renderNotifications(activity, container, notifications, anchor, prefs)
                    } else {
                        messageText.text = "Failed to load notifications."
                    }
                }

                override fun onFailure(call: Call<List<NotificationResponse>>, t: Throwable) {
                    messageText.text = "Failed to load notifications."
                }
            })
    }

    private fun renderNotifications(
        activity: AppCompatActivity,
        container: LinearLayout,
        notifications: List<NotificationResponse>,
        bell: View,
        prefs: SharedPreferences
    ) {
        container.removeAllViews()

        notifications.forEach { notification ->
            val card = LayoutInflater.from(activity)
                .inflate(R.layout.item_notification_dropdown_card, container, false)

            val title = card.findViewById<TextView>(R.id.tvDropdownNotificationTitle)
            val message = card.findViewById<TextView>(R.id.tvDropdownNotificationMessage)
            val status = card.findViewById<TextView>(R.id.tvDropdownNotificationStatus)

            title.text = notification.title ?: "Notification"
            message.text = notification.message ?: "-"
            status.text = if (notification.isRead == true) "Read" else "Unread"

            card.setOnClickListener {
                val id = notification.id

                if (id == null || notification.isRead == true) {
                    return@setOnClickListener
                }

                RetrofitClient.instance.markNotificationAsRead(id)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    "Notification marked as read.",
                                    Toast.LENGTH_SHORT
                                ).show()

                                if (bell is TextView) {
                                    loadUnreadCount(bell, prefs)
                                }

                                status.text = "Read"
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(
                                activity,
                                "Failed to update notification.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }

            container.addView(card)
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }

        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "US"
        }
    }

    private fun dpToPx(context: Context, value: Int): Int {
        return (value * context.resources.displayMetrics.density).toInt()
    }
}