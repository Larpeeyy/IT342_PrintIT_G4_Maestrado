package com.printit.mobile.features.admin.payments

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

class AdminPaymentsActivity : AppCompatActivity() {

    private lateinit var tvPaymentsMessage: TextView
    private lateinit var paymentsContainer: LinearLayout
    private lateinit var btnDashboard: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_admin_payments)

        tvPaymentsMessage = findViewById(R.id.tvPaymentsMessage)
        paymentsContainer = findViewById(R.id.paymentsContainer)
        btnDashboard = findViewById(R.id.btnDashboard)

        btnDashboard.setOnClickListener {
            startActivity(Intent(this, AdminDashboardActivity::class.java))
            finish()
        }

        loadPayments()
    }

    private fun loadPayments() {
        tvPaymentsMessage.text = "Loading payments..."
        paymentsContainer.removeAllViews()

        RetrofitClient.instance.getAdminPayments()
            .enqueue(object : Callback<List<AdminPaymentResponse>> {
                override fun onResponse(
                    call: Call<List<AdminPaymentResponse>>,
                    response: Response<List<AdminPaymentResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderPayments(response.body()!!)
                    } else {
                        tvPaymentsMessage.text = "Failed to load payments."
                    }
                }

                override fun onFailure(call: Call<List<AdminPaymentResponse>>, t: Throwable) {
                    tvPaymentsMessage.text = "Failed to load payments."
                    Toast.makeText(
                        this@AdminPaymentsActivity,
                        "Payments error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderPayments(payments: List<AdminPaymentResponse>) {
        paymentsContainer.removeAllViews()

        if (payments.isEmpty()) {
            tvPaymentsMessage.text = "No payments found."
            return
        }

        tvPaymentsMessage.text = "Showing ${payments.size} payment record(s)."

        payments.forEach { payment ->
            val card: View = layoutInflater.inflate(
                R.layout.item_admin_payment_card,
                paymentsContainer,
                false
            )

            val tvPaymentCode: TextView = card.findViewById(R.id.tvPaymentCode)
            val tvPaymentStudent: TextView = card.findViewById(R.id.tvPaymentStudent)
            val tvPaymentDetails: TextView = card.findViewById(R.id.tvPaymentDetails)
            val tvPaymentStatus: TextView = card.findViewById(R.id.tvPaymentStatus)
            val tvPaymentAmount: TextView = card.findViewById(R.id.tvPaymentAmount)

            tvPaymentCode.text = payment.paymentCode ?: "PAY-${payment.id ?: "-"}"
            tvPaymentStudent.text = payment.studentName ?: "Unknown Student"
            tvPaymentDetails.text =
                "${payment.orderCode ?: "No order"} • ${payment.fileName ?: "Untitled file"} • ${payment.provider ?: "-"}"
            tvPaymentStatus.text = payment.status ?: "Pending"
            tvPaymentAmount.text = "P %.2f".format(payment.amount ?: 0.0)

            paymentsContainer.addView(card)
        }
    }
}