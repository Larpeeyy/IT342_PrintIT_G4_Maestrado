package com.printit.mobile.features.admin.orders

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

class AdminOrdersActivity : AppCompatActivity() {

    private lateinit var tvOrdersMessage: TextView
    private lateinit var ordersContainer: LinearLayout
    private lateinit var btnDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_orders)

        tvOrdersMessage = findViewById(R.id.tvOrdersMessage)
        ordersContainer = findViewById(R.id.ordersContainer)
        btnDashboard = findViewById(R.id.btnDashboard)

        btnDashboard.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        loadOrders()
    }

    private fun loadOrders() {
        tvOrdersMessage.text = "Loading orders..."
        ordersContainer.removeAllViews()

        RetrofitClient.instance.getAdminOrders()
            .enqueue(object : Callback<List<AdminOrderResponse>> {
                override fun onResponse(
                    call: Call<List<AdminOrderResponse>>,
                    response: Response<List<AdminOrderResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderOrders(response.body()!!)
                    } else {
                        tvOrdersMessage.text = "Failed to load orders."
                    }
                }

                override fun onFailure(call: Call<List<AdminOrderResponse>>, t: Throwable) {
                    tvOrdersMessage.text = "Failed to load orders."
                    Toast.makeText(
                        this@AdminOrdersActivity,
                        "Orders error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderOrders(orders: List<AdminOrderResponse>) {
        ordersContainer.removeAllViews()

        if (orders.isEmpty()) {
            tvOrdersMessage.text = "No orders found."
            return
        }

        tvOrdersMessage.text = "Showing ${orders.size} order(s)."

        orders.forEach { order ->
            val card: View = layoutInflater.inflate(
                R.layout.item_admin_order_card,
                ordersContainer,
                false
            )

            val tvFileName: TextView = card.findViewById(R.id.tvFileName)
            val tvStudentName: TextView = card.findViewById(R.id.tvStudentName)
            val tvOrderDetails: TextView = card.findViewById(R.id.tvOrderDetails)
            val tvOrderStatus: TextView = card.findViewById(R.id.tvOrderStatus)

            tvFileName.text = order.fileName ?: "Untitled file"
            tvStudentName.text = order.studentName ?: "Unknown Student"
            tvOrderDetails.text = "${order.copies ?: 0L} copies • ${order.dateSubmitted ?: "-"}"
            tvOrderStatus.text = order.status ?: "Pending"

            ordersContainer.addView(card)
        }
    }
}