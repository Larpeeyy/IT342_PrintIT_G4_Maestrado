package com.printit.mobile.features.student.payments

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

class StudentPaymentsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationBell: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvTransactions: TextView
    private lateinit var tvAverageOrder: TextView
    private lateinit var etPaymentSearch: EditText
    private lateinit var spPaymentStatus: Spinner
    private lateinit var tvPaymentsCount: TextView
    private lateinit var paymentsContainer: LinearLayout
    private lateinit var btnBackToDashboard: TextView

    private var allPayments: List<StudentPaymentResponse> = emptyList()
    private var selectedStatus = "All Status"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_student_payments)

        bindViews()
        setupTopbar()
        setupStatusFilter()
        setupSearch()
        setupButtons()
        loadPayments()
    }

    override fun onResume() {
        super.onResume()
        setupTopbar()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvNotificationBell = findViewById(R.id.tvNotificationBell)
        tvTotalSpent = findViewById(R.id.tvTotalSpent)
        tvTransactions = findViewById(R.id.tvTransactions)
        tvAverageOrder = findViewById(R.id.tvAverageOrder)
        etPaymentSearch = findViewById(R.id.etPaymentSearch)
        spPaymentStatus = findViewById(R.id.spPaymentStatus)
        tvPaymentsCount = findViewById(R.id.tvPaymentsCount)
        paymentsContainer = findViewById(R.id.paymentsContainer)
        btnBackToDashboard = findViewById(R.id.btnBackToDashboard)
    }

    private fun setupTopbar() {
        MobileTopbarHelper.setup(this, tvProfileBadge, tvNotificationBell)
    }

    private fun setupStatusFilter() {
        val statuses = listOf(
            "All Status",
            "Completed",
            "Pending",
            "Failed"
        )

        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_selected_item,
            statuses
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        spPaymentStatus.adapter = adapter
        spPaymentStatus.setSelection(0)

        spPaymentStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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

                renderPayments()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedStatus = "All Status"
                renderPayments()
            }
        }
    }

    private fun setupSearch() {
        etPaymentSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                renderPayments()
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

    private fun loadPayments() {
        val email = sharedPreferences.getString("email", "") ?: ""

        if (email.isBlank()) {
            Toast.makeText(this, "No logged-in user found.", Toast.LENGTH_SHORT).show()
            return
        }

        tvPaymentsCount.text = "Loading payments..."

        RetrofitClient.instance.getStudentPayments(email)
            .enqueue(object : Callback<List<StudentPaymentResponse>> {
                override fun onResponse(
                    call: Call<List<StudentPaymentResponse>>,
                    response: Response<List<StudentPaymentResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        allPayments = response.body() ?: emptyList()
                        updateSummaryCards()
                        renderPayments()
                    } else {
                        tvPaymentsCount.text = "Failed to load payments."
                    }
                }

                override fun onFailure(call: Call<List<StudentPaymentResponse>>, t: Throwable) {
                    tvPaymentsCount.text = "Failed to load payments."

                    Toast.makeText(
                        this@StudentPaymentsActivity,
                        "Payments error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun updateSummaryCards() {
        val completedPayments = allPayments.filter {
            (it.status ?: "").equals("Completed", ignoreCase = true)
        }

        val totalSpent = completedPayments.sumOf {
            it.amount ?: 0.0
        }

        val transactionCount = allPayments.size

        val average = if (transactionCount > 0) {
            totalSpent / transactionCount
        } else {
            0.0
        }

        tvTotalSpent.text = "P ${formatMoney(totalSpent)}"
        tvTransactions.text = transactionCount.toString()
        tvAverageOrder.text = "P ${formatMoney(average)}"
    }

    private fun renderPayments() {
        val query = etPaymentSearch.text.toString().trim().lowercase()

        val filteredPayments = allPayments.filter { payment ->
            val matchesSearch =
                query.isBlank() ||
                        (payment.paymentCode ?: "").lowercase().contains(query) ||
                        (payment.orderCode ?: "").lowercase().contains(query) ||
                        (payment.fileName ?: "").lowercase().contains(query)

            val matchesStatus =
                selectedStatus == "All Status" ||
                        (payment.status ?: "").equals(selectedStatus, ignoreCase = true)

            matchesSearch && matchesStatus
        }

        paymentsContainer.removeAllViews()

        tvPaymentsCount.text = "${filteredPayments.size} transaction(s) found"

        if (filteredPayments.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No payments found."
            emptyText.setTextColor(Color.parseColor("#667085"))
            emptyText.textSize = 14f
            emptyText.setPadding(0, dp(18), 0, dp(18))
            paymentsContainer.addView(emptyText)
            return
        }

        for (payment in filteredPayments) {
            paymentsContainer.addView(createPaymentCard(payment))
        }
    }

    private fun createPaymentCard(payment: StudentPaymentResponse): LinearLayout {
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

        val paymentCode = TextView(this)
        paymentCode.text = payment.paymentCode ?: "PAY-${payment.id ?: "-"}"
        paymentCode.setTextColor(Color.parseColor("#111827"))
        paymentCode.textSize = 14f
        paymentCode.setTypeface(null, Typeface.BOLD)

        val amount = TextView(this)
        amount.text = "P ${formatMoney(payment.amount ?: 0.0)}"
        amount.setTextColor(Color.parseColor("#111827"))
        amount.textSize = 14f
        amount.gravity = Gravity.END
        amount.setTypeface(null, Typeface.BOLD)

        headerRow.addView(
            paymentCode,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        headerRow.addView(
            amount,
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        )

        card.addView(headerRow)

        val orderText = TextView(this)
        orderText.text = payment.orderCode ?: "-"
        orderText.setTextColor(Color.parseColor("#111827"))
        orderText.textSize = 15f
        orderText.setTypeface(null, Typeface.BOLD)
        orderText.setPadding(0, dp(10), 0, dp(4))
        card.addView(orderText)

        val fileText = TextView(this)
        fileText.text = payment.fileName ?: "-"
        fileText.setTextColor(Color.parseColor("#475467"))
        fileText.textSize = 13f
        card.addView(fileText)

        val methodText = TextView(this)
        methodText.text = "Method: ${payment.provider ?: "Sandbox"}"
        methodText.setTextColor(Color.parseColor("#475467"))
        methodText.textSize = 13f
        methodText.setPadding(0, dp(4), 0, 0)
        card.addView(methodText)

        val bottomRow = LinearLayout(this)
        bottomRow.orientation = LinearLayout.HORIZONTAL
        bottomRow.gravity = Gravity.CENTER_VERTICAL
        bottomRow.setPadding(0, dp(12), 0, 0)

        val status = TextView(this)
        status.text = payment.status ?: "Completed"
        status.gravity = Gravity.CENTER
        status.textSize = 12f
        status.setPadding(dp(12), dp(6), dp(12), dp(6))
        status.setTextColor(getPaymentStatusTextColor(payment.status))
        status.setBackgroundResource(getPaymentStatusBackground(payment.status))

        val date = TextView(this)
        date.text = formatDate(payment.createdAt)
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

    private fun getPaymentStatusBackground(status: String?): Int {
        return when (status?.lowercase()) {
            "completed" -> R.drawable.bg_status_ready
            "pending" -> R.drawable.bg_status_pending
            "failed" -> R.drawable.bg_status_completed
            else -> R.drawable.bg_status_ready
        }
    }

    private fun getPaymentStatusTextColor(status: String?): Int {
        return when (status?.lowercase()) {
            "completed" -> Color.parseColor("#15803D")
            "pending" -> Color.parseColor("#A16207")
            "failed" -> Color.parseColor("#B91C1C")
            else -> Color.parseColor("#15803D")
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