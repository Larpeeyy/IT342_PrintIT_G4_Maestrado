package com.printit.mobile.features.student.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.student.neworder.NewOrderActivity
import com.printit.mobile.features.student.orders.StudentOrdersActivity
import com.printit.mobile.features.student.payments.StudentPaymentsActivity
import com.printit.mobile.shared.components.MobileTopbarHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentDashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationBell: TextView
    private lateinit var tvWelcomeTitle: TextView
    private lateinit var tvWelcomeMessage: TextView

    private lateinit var tvTotalOrders: TextView
    private lateinit var tvPendingOrders: TextView
    private lateinit var tvReadyOrders: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvRecentOrders: TextView

    private lateinit var navStudentNewOrder: TextView
    private lateinit var navStudentOrders: TextView
    private lateinit var navStudentPayments: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_student_dashboard)

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

        tvTotalOrders = findViewById(R.id.tvTotalOrders)
        tvPendingOrders = findViewById(R.id.tvPendingOrders)
        tvReadyOrders = findViewById(R.id.tvReadyOrders)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvRecentOrders = findViewById(R.id.tvRecentOrders)

        navStudentNewOrder = findViewById(R.id.navStudentNewOrder)
        navStudentOrders = findViewById(R.id.navStudentOrders)
        navStudentPayments = findViewById(R.id.navStudentPayments)
    }

    private fun setupUserHeader() {
        MobileTopbarHelper.setup(this, tvProfileBadge, tvNotificationBell)

        tvWelcomeTitle.text = "Welcome back, Student!"
        tvWelcomeMessage.text = "Here's what's happening with your print orders."
    }

    private fun setupButtons() {
        navStudentNewOrder.setOnClickListener {
            startActivity(Intent(this, NewOrderActivity::class.java))
        }

        navStudentOrders.setOnClickListener {
            startActivity(Intent(this, StudentOrdersActivity::class.java))
        }

        navStudentPayments.setOnClickListener {
            startActivity(Intent(this, StudentPaymentsActivity::class.java))
        }
    }

    private fun loadDashboard() {
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isBlank()) {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show()
            return
        }

        setLoadingState()

        RetrofitClient.instance.getStudentDashboard(email)
            .enqueue(object : Callback<StudentDashboardResponse> {
                override fun onResponse(
                    call: Call<StudentDashboardResponse>,
                    response: Response<StudentDashboardResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderDashboard(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@StudentDashboardActivity,
                            "Failed to load student dashboard.",
                            Toast.LENGTH_SHORT
                        ).show()
                        renderEmptyState()
                    }
                }

                override fun onFailure(call: Call<StudentDashboardResponse>, t: Throwable) {
                    Toast.makeText(
                        this@StudentDashboardActivity,
                        "Dashboard error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    renderEmptyState()
                }
            })
    }

    private fun setLoadingState() {
        tvTotalOrders.text = "..."
        tvPendingOrders.text = "..."
        tvReadyOrders.text = "..."
        tvTotalSpent.text = "P ..."
        tvRecentOrders.text = "Loading recent orders..."
    }

    private fun renderDashboard(data: StudentDashboardResponse) {
        val readyCount = data.readyForPickupOrders ?: data.readyForPickup ?: 0L

        tvTotalOrders.text = (data.totalOrders ?: 0L).toString()
        tvPendingOrders.text = (data.pendingOrders ?: 0L).toString()
        tvReadyOrders.text = readyCount.toString()
        tvTotalSpent.text = "P %.2f".format(data.totalSpent ?: 0.0)

        val recentOrders = data.recentOrders.orEmpty()

        tvRecentOrders.text = if (recentOrders.isEmpty()) {
            "No recent orders yet."
        } else {
            recentOrders.take(3).joinToString(separator = "\n\n") { order ->
                val amount = "P %.2f".format(order.totalAmount ?: 0.0)
                val status = order.status ?: "Pending"
                val code = order.orderCode ?: "ORD-${order.id ?: "-"}"

                "${order.fileName ?: "Untitled file"}\n$code • $status • $amount"
            }
        }
    }

    private fun renderEmptyState() {
        tvTotalOrders.text = "0"
        tvPendingOrders.text = "0"
        tvReadyOrders.text = "0"
        tvTotalSpent.text = "P 0.00"
        tvRecentOrders.text = "No recent orders yet."
    }
}