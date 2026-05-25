package com.printit.mobile.features.staff.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.staff.orders.StaffOrdersActivity
import com.printit.mobile.shared.components.MobileTopbarHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaffDashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationBell: TextView
    private lateinit var tvWelcomeTitle: TextView
    private lateinit var tvWelcomeMessage: TextView

    private lateinit var tvPendingOrders: TextView
    private lateinit var tvPrintingOrders: TextView
    private lateinit var tvReadyOrders: TextView
    private lateinit var tvCompletedToday: TextView
    private lateinit var tvRecentOrders: TextView

    private lateinit var navStaffOrders: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_staff_dashboard)

        bindViews()
        setupUserHeader()
        setupButtons()
        loadDashboard()
    }

    override fun onResume() {
        super.onResume()
        setupUserHeader()
        loadDashboard()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvNotificationBell = findViewById(R.id.tvNotificationBell)
        tvWelcomeTitle = findViewById(R.id.tvWelcomeTitle)
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)

        tvPendingOrders = findViewById(R.id.tvPendingOrders)
        tvPrintingOrders = findViewById(R.id.tvPrintingOrders)
        tvReadyOrders = findViewById(R.id.tvReadyOrders)
        tvCompletedToday = findViewById(R.id.tvCompletedToday)
        tvRecentOrders = findViewById(R.id.tvRecentOrders)

        navStaffOrders = findViewById(R.id.navStaffOrders)
    }

    private fun setupUserHeader() {
        MobileTopbarHelper.setup(this, tvProfileBadge, tvNotificationBell)

        tvWelcomeTitle.text = "Staff Dashboard"
        tvWelcomeMessage.text = "Manage and process student print requests."
    }

    private fun setupButtons() {
        navStaffOrders.setOnClickListener {
            startActivity(Intent(this, StaffOrdersActivity::class.java))
        }
    }

    private fun loadDashboard() {
        setLoadingState()

        RetrofitClient.instance.getStaffDashboard()
            .enqueue(object : Callback<StaffDashboardResponse> {
                override fun onResponse(
                    call: Call<StaffDashboardResponse>,
                    response: Response<StaffDashboardResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderDashboard(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@StaffDashboardActivity,
                            "Failed to load staff dashboard.",
                            Toast.LENGTH_SHORT
                        ).show()
                        renderEmptyState()
                    }
                }

                override fun onFailure(call: Call<StaffDashboardResponse>, t: Throwable) {
                    Toast.makeText(
                        this@StaffDashboardActivity,
                        "Dashboard error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    renderEmptyState()
                }
            })
    }

    private fun setLoadingState() {
        tvPendingOrders.text = "..."
        tvPrintingOrders.text = "..."
        tvReadyOrders.text = "..."
        tvCompletedToday.text = "..."
        tvRecentOrders.text = "Loading recent orders..."
    }

    private fun renderDashboard(data: StaffDashboardResponse) {
        tvPendingOrders.text = (data.pendingOrders ?: 0L).toString()
        tvPrintingOrders.text = (data.printingOrders ?: 0L).toString()
        tvReadyOrders.text = (data.readyForPickupOrders ?: 0L).toString()
        tvCompletedToday.text = (data.completedToday ?: 0L).toString()

        val recentOrders = data.recentOrders.orEmpty()

        tvRecentOrders.text = if (recentOrders.isEmpty()) {
            "No recent orders yet."
        } else {
            recentOrders.take(5).joinToString(separator = "\n\n") { order ->
                val status = order.status ?: "Pending"
                val copies = order.copies ?: 0L

                "${order.fileName ?: "Untitled file"}\n${order.studentName ?: "Unknown Student"} • $copies copies • $status"
            }
        }
    }

    private fun renderEmptyState() {
        tvPendingOrders.text = "0"
        tvPrintingOrders.text = "0"
        tvReadyOrders.text = "0"
        tvCompletedToday.text = "0"
        tvRecentOrders.text = "No recent orders yet."
    }
}