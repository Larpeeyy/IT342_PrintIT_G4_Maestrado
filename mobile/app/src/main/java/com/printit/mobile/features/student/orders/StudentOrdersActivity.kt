package com.printit.mobile.features.student.orders

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.student.dashboard.StudentDashboardActivity
import com.printit.mobile.shared.components.MobileTopbarHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentOrdersActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationBell: TextView
    private lateinit var etOrderSearch: EditText
    private lateinit var spOrderStatus: Spinner
    private lateinit var tvOrdersCount: TextView
    private lateinit var ordersContainer: LinearLayout
    private lateinit var btnBackToDashboard: TextView

    private var allOrders: List<StudentOrderResponse> = emptyList()
    private var selectedStatus = "All Status"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_student_orders)

        bindViews()
        setupTopbar()
        setupStatusFilter()
        setupSearch()
        setupButtons()
        loadOrders()
    }

    override fun onResume() {
        super.onResume()
        setupTopbar()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvNotificationBell = findViewById(R.id.tvNotificationBell)
        etOrderSearch = findViewById(R.id.etOrderSearch)
        spOrderStatus = findViewById(R.id.spOrderStatus)
        tvOrdersCount = findViewById(R.id.tvOrdersCount)
        ordersContainer = findViewById(R.id.ordersContainer)
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard)
    }

    private fun setupTopbar() {
        MobileTopbarHelper.setup(this, tvProfileBadge, tvNotificationBell)
    }

    private fun setupStatusFilter() {
        val statuses = listOf(
            "All Status",
            "Pending",
            "Printing",
            "Ready for Pickup",
            "Completed"
        )

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_selected_item,
            statuses
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        spOrderStatus.adapter = adapter
        spOrderStatus.setSelection(0)

        spOrderStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                selectedStatus = statuses[position]

                if (view is TextView) {
                    view.text = selectedStatus
                    view.setTextColor(Color.parseColor("#111827"))
                }

                renderOrders()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedStatus = "All Status"
                renderOrders()
            }
        }
    }

    private fun setupSearch() {
        etOrderSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                renderOrders()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupButtons() {
        btnBackToDashboard.setOnClickListener {
            startActivity(Intent(this, StudentDashboardActivity::class.java))
            finish()
        }
    }

    private fun loadOrders() {
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isBlank()) {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show()
            return
        }

        tvOrdersCount.text = "Loading orders..."

        RetrofitClient.instance.getStudentOrders(email)
            .enqueue(object : Callback<List<StudentOrderResponse>> {
                override fun onResponse(
                    call: Call<List<StudentOrderResponse>>,
                    response: Response<List<StudentOrderResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        allOrders = response.body() ?: emptyList()
                        renderOrders()
                    } else {
                        tvOrdersCount.text = "Failed to load orders."
                    }
                }

                override fun onFailure(call: Call<List<StudentOrderResponse>>, t: Throwable) {
                    tvOrdersCount.text = "Failed to load orders."
                    Toast.makeText(
                        this@StudentOrdersActivity,
                        "Orders error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderOrders() {
        val query = etOrderSearch.text.toString().trim().lowercase()

        val filteredOrders = allOrders.filter { order ->
            val matchesSearch =
                query.isBlank() ||
                        (order.orderCode ?: "").lowercase().contains(query) ||
                        (order.fileName ?: "").lowercase().contains(query)

            val matchesStatus =
                selectedStatus == "All Status" ||
                        (order.status ?: "").equals(selectedStatus, ignoreCase = true)

            matchesSearch && matchesStatus
        }

        ordersContainer.removeAllViews()

        tvOrdersCount.text = "${filteredOrders.size} order(s) found"

        if (filteredOrders.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No orders found."
            emptyText.setTextColor(Color.parseColor("#667085"))
            emptyText.textSize = 14f
            emptyText.setPadding(0, dp(18), 0, dp(18))
            ordersContainer.addView(emptyText)
            return
        }

        for (order in filteredOrders) {
            ordersContainer.addView(createOrderCard(order))
        }
    }

    private fun createOrderCard(order: StudentOrderResponse): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(dp(14), dp(14), dp(14), dp(14))
        card.setBackgroundResource(R.drawable.bg_card_white)

        val cardParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        cardParams.setMargins(0, 0, 0, dp(12))
        card.layoutParams = cardParams

        val headerRow = LinearLayout(this)
        headerRow.orientation = LinearLayout.HORIZONTAL
        headerRow.gravity = Gravity.CENTER_VERTICAL

        val orderCode = TextView(this)
        orderCode.text = order.orderCode ?: "ORD-${order.id ?: "-"}"
        orderCode.setTextColor(Color.parseColor("#111827"))
        orderCode.textSize = 14f
        orderCode.setTypeface(null, Typeface.BOLD)

        val amount = TextView(this)
        amount.text = "P ${formatMoney(order.totalAmount ?: 0.0)}"
        amount.setTextColor(Color.parseColor("#111827"))
        amount.textSize = 14f
        amount.gravity = Gravity.END
        amount.setTypeface(null, Typeface.BOLD)

        headerRow.addView(
            orderCode,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        headerRow.addView(
            amount,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        card.addView(headerRow)

        val fileName = TextView(this)
        fileName.text = order.fileName ?: "Untitled file"
        fileName.setTextColor(Color.parseColor("#111827"))
        fileName.textSize = 15f
        fileName.setTypeface(null, Typeface.BOLD)
        fileName.setPadding(0, dp(10), 0, dp(4))
        card.addView(fileName)

        val details = TextView(this)
        details.text =
            "${order.paperSize ?: "-"}, ${order.colorMode ?: "-"}, ${order.copies ?: 0} copy/copies"
        details.setTextColor(Color.parseColor("#475467"))
        details.textSize = 13f
        card.addView(details)

        val bottomRow = LinearLayout(this)
        bottomRow.orientation = LinearLayout.HORIZONTAL
        bottomRow.gravity = Gravity.CENTER_VERTICAL
        bottomRow.setPadding(0, dp(12), 0, 0)

        val status = TextView(this)
        status.text = order.status ?: "Pending"
        status.gravity = Gravity.CENTER
        status.textSize = 12f
        status.setPadding(dp(12), dp(6), dp(12), dp(6))
        status.setTextColor(getStatusTextColor(order.status))
        status.setBackgroundResource(getStatusBackground(order.status))

        val date = TextView(this)
        date.text = formatDate(order.createdAt)
        date.gravity = Gravity.END
        date.setTextColor(Color.parseColor("#667085"))
        date.textSize = 12f

        bottomRow.addView(
            status,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )

        bottomRow.addView(
            date,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        card.addView(bottomRow)

        return card
    }

    private fun getStatusBackground(status: String?): Int {
        return when (status?.lowercase()) {
            "pending" -> R.drawable.bg_status_pending
            "printing" -> R.drawable.bg_status_printing
            "ready for pickup" -> R.drawable.bg_status_ready
            "completed" -> R.drawable.bg_status_completed
            else -> R.drawable.bg_status_completed
        }
    }

    private fun getStatusTextColor(status: String?): Int {
        return when (status?.lowercase()) {
            "pending" -> Color.parseColor("#A16207")
            "printing" -> Color.parseColor("#1D4ED8")
            "ready for pickup" -> Color.parseColor("#15803D")
            "completed" -> Color.parseColor("#374151")
            else -> Color.parseColor("#374151")
        }
    }

    private fun formatMoney(value: Double): String {
        return String.format("%.2f", value)
    }

    private fun formatDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) {
            return ""
        }

        return rawDate
            .substringBefore("T")
            .ifBlank { rawDate }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}