package com.printit.mobile.features.staff.orders

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
import com.printit.mobile.features.auth.LoginActivity
import com.printit.mobile.features.staff.dashboard.StaffDashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaffOrdersActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvOrdersMessage: TextView
    private lateinit var ordersContainer: LinearLayout

    private lateinit var btnDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_staff_orders)

        bindViews()
        setupHeader()
        setupButtons()
        loadOrders()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvOrdersMessage = findViewById(R.id.tvOrdersMessage)
        ordersContainer = findViewById(R.id.ordersContainer)

        btnDashboard = findViewById(R.id.btnDashboard)
    }

    private fun setupHeader() {
        val fullName = sharedPreferences.getString("fullName", "Staff") ?: "Staff"
        tvProfileBadge.text = getInitials(fullName)
    }

    private fun setupButtons() {
        btnDashboard.setOnClickListener {
            startActivity(Intent(this, StaffDashboardActivity::class.java))
            finish()
        }
    }

    private fun loadOrders() {
        tvOrdersMessage.text = "Loading orders queue..."
        ordersContainer.removeAllViews()

        RetrofitClient.instance.getStaffOrders()
            .enqueue(object : Callback<List<StaffOrderResponse>> {
                override fun onResponse(
                    call: Call<List<StaffOrderResponse>>,
                    response: Response<List<StaffOrderResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderOrders(response.body()!!)
                    } else {
                        tvOrdersMessage.text = "Failed to load orders."
                    }
                }

                override fun onFailure(call: Call<List<StaffOrderResponse>>, t: Throwable) {
                    tvOrdersMessage.text = "Failed to load orders."
                    Toast.makeText(
                        this@StaffOrdersActivity,
                        "Orders error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderOrders(orders: List<StaffOrderResponse>) {
        ordersContainer.removeAllViews()

        if (orders.isEmpty()) {
            tvOrdersMessage.text = "No orders found."
            return
        }

        tvOrdersMessage.text = "Showing ${orders.size} order(s)."

        orders.forEach { order ->
            val card: View = layoutInflater.inflate(
                R.layout.item_staff_order_card,
                ordersContainer,
                false
            )

            val tvFileName: TextView = card.findViewById(R.id.tvFileName)
            val tvStudentName: TextView = card.findViewById(R.id.tvStudentName)
            val tvOrderDetails: TextView = card.findViewById(R.id.tvOrderDetails)
            val tvOrderStatus: TextView = card.findViewById(R.id.tvOrderStatus)
            val btnViewOrder: Button = card.findViewById(R.id.btnViewOrder)

            tvFileName.text = order.fileName ?: "Untitled file"
            tvStudentName.text = order.studentName ?: "Unknown Student"
            tvOrderDetails.text = "${order.copies ?: 0L} copies • ${order.dateSubmitted ?: "-"}"
            tvOrderStatus.text = order.status ?: "Pending"

            btnViewOrder.setOnClickListener {
                val intent = Intent(this, StaffViewOrderActivity::class.java)
                intent.putExtra("orderId", order.id ?: 0L)
                startActivity(intent)
            }

            ordersContainer.addView(card)
        }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }

        return when {
            parts.size >= 2 -> "${parts[0].first()}${parts[1].first()}".uppercase()
            parts.size == 1 -> parts[0].take(2).uppercase()
            else -> "SF"
        }
    }
}