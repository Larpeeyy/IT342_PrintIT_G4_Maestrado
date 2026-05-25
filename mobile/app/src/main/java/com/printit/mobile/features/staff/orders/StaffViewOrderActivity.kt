package com.printit.mobile.features.staff.orders

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.core.util.Constants
import com.printit.mobile.features.staff.dashboard.StaffDashboardActivity
import com.printit.mobile.shared.components.MobileTopbarHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StaffViewOrderActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private var orderId: Long = 0L
    private var hasSavedFileUrl: Boolean = false
    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationBell: TextView

    private lateinit var navStaffDashboard: TextView
    private lateinit var navStaffOrders: TextView

    private lateinit var tvOrderTitle: TextView
    private lateinit var tvSubmittedDate: TextView
    private lateinit var tvFileNamePreview: TextView
    private lateinit var tvOrderCode: TextView
    private lateinit var tvStudentName: TextView
    private lateinit var tvStudentEmail: TextView
    private lateinit var tvOrderDetails: TextView
    private lateinit var tvOrderAmount: TextView
    private lateinit var tvStatusChip: TextView

    private lateinit var stepPendingIcon: TextView
    private lateinit var stepPendingText: TextView
    private lateinit var stepPrintingIcon: TextView
    private lateinit var stepPrintingText: TextView
    private lateinit var stepReadyIcon: TextView
    private lateinit var stepReadyText: TextView
    private lateinit var stepCompletedIcon: TextView
    private lateinit var stepCompletedText: TextView

    private lateinit var spinnerStatus: Spinner
    private lateinit var btnDownloadFile: TextView
    private lateinit var btnUpdateStatus: TextView
    private lateinit var btnMarkCompleted: TextView
    private lateinit var btnBack: TextView

    private val statusOptions = listOf(
        "Pending",
        "Printing",
        "Ready for Pickup",
        "Completed"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)
        orderId = intent.getLongExtra("orderId", 0L)

        setContentView(R.layout.activity_staff_view_order)

        bindViews()
        setupTopbar()
        setupSpinner()
        setupButtons()
        loadOrder()
    }

    override fun onResume() {
        super.onResume()
        setupTopbar()
    }

    private fun bindViews() {
        tvProfileBadge = findViewById(R.id.tvProfileBadge)
        tvNotificationBell = findViewById(R.id.tvNotificationBell)

        navStaffDashboard = findViewById(R.id.navStaffDashboard)
        navStaffOrders = findViewById(R.id.navStaffOrders)

        tvOrderTitle = findViewById(R.id.tvOrderTitle)
        tvSubmittedDate = findViewById(R.id.tvSubmittedDate)
        tvFileNamePreview = findViewById(R.id.tvFileNamePreview)
        tvOrderCode = findViewById(R.id.tvOrderCode)
        tvStudentName = findViewById(R.id.tvStudentName)
        tvStudentEmail = findViewById(R.id.tvStudentEmail)
        tvOrderDetails = findViewById(R.id.tvOrderDetails)
        tvOrderAmount = findViewById(R.id.tvOrderAmount)
        tvStatusChip = findViewById(R.id.tvStatusChip)

        stepPendingIcon = findViewById(R.id.stepPendingIcon)
        stepPendingText = findViewById(R.id.stepPendingText)
        stepPrintingIcon = findViewById(R.id.stepPrintingIcon)
        stepPrintingText = findViewById(R.id.stepPrintingText)
        stepReadyIcon = findViewById(R.id.stepReadyIcon)
        stepReadyText = findViewById(R.id.stepReadyText)
        stepCompletedIcon = findViewById(R.id.stepCompletedIcon)
        stepCompletedText = findViewById(R.id.stepCompletedText)

        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnDownloadFile = findViewById(R.id.btnDownloadFile)
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus)
        btnMarkCompleted = findViewById(R.id.btnMarkCompleted)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupTopbar() {
        MobileTopbarHelper.setup(this, tvProfileBadge, tvNotificationBell)
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            R.layout.spinner_selected_item,
            statusOptions
        )

        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        spinnerStatus.adapter = adapter
        spinnerStatus.setSelection(0)

        spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: android.view.View?,
                position: Int,
                id: Long
            ) {
                if (view is TextView) {
                    view.text = statusOptions[position]
                    view.setTextColor(Color.parseColor("#111827"))
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        navStaffDashboard.setOnClickListener {
            startActivity(Intent(this, StaffDashboardActivity::class.java))
            finish()
        }

        navStaffOrders.setOnClickListener {
            finish()
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnDownloadFile.setOnClickListener {
            openFileDownload()
        }

        btnUpdateStatus.setOnClickListener {
            updateStatus(spinnerStatus.selectedItem.toString())
        }

        btnMarkCompleted.setOnClickListener {
            updateStatus("Completed")
        }
    }

    private fun loadOrder() {
        if (orderId <= 0L) {
            Toast.makeText(this, "Invalid order selected.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        RetrofitClient.instance.getStaffOrderDetails(orderId)
            .enqueue(object : Callback<StaffOrderDetailsResponse> {
                override fun onResponse(
                    call: Call<StaffOrderDetailsResponse>,
                    response: Response<StaffOrderDetailsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderOrder(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@StaffViewOrderActivity,
                            "Failed to load order details.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<StaffOrderDetailsResponse>, t: Throwable) {
                    Toast.makeText(
                        this@StaffViewOrderActivity,
                        "Order error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderOrder(order: StaffOrderDetailsResponse) {
        val fileName = order.fileName ?: "Untitled file"
        val code = order.orderCode ?: "ORD-${order.id ?: "-"}"
        val studentName = order.studentName ?: "Unknown Student"
        val studentEmail = order.email ?: "-"
        val paperSize = order.paperSize ?: "-"
        val colorMode = order.colorMode ?: "-"
        val copies = order.copies ?: 0L
        val totalAmount = order.totalAmount ?: 0.0
        val status = normalizeStatus(order.status ?: "Pending")
        hasSavedFileUrl = !order.fileUrl.isNullOrBlank()

        tvOrderTitle.text = "Order $code"
        tvSubmittedDate.text = "Review and update student print order."

        tvFileNamePreview.text = fileName
        tvOrderCode.text = "Order Code: $code"
        tvStudentName.text = "Student Name: $studentName"
        tvStudentEmail.text = "Email: $studentEmail"
        tvOrderDetails.text = "$paperSize • $colorMode • $copies copies"
        tvOrderAmount.text = "Total: P %.2f".format(totalAmount)

        tvStatusChip.text = status
        applyStatusChipColor(status)
        applyProgressColor(status)

        val index = statusOptions.indexOf(status)
        spinnerStatus.setSelection(if (index >= 0) index else 0)

        btnDownloadFile.isEnabled = hasSavedFileUrl
        btnDownloadFile.text = if (hasSavedFileUrl) {
            "Download File"
        } else {
            "No File Available"
        }
    }

    private fun openFileDownload() {
        if (!hasSavedFileUrl) {
            Toast.makeText(
                this,
                "File download is not available because this order has no saved file URL.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (orderId <= 0L) {
            Toast.makeText(
                this,
                "File download is not available. This order could not be found.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        try {
            val downloadUrl = "${Constants.BASE_URL.trimEnd('/')}/api/staff/orders/$orderId/download"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
            startActivity(intent)
        } catch (error: Exception) {
            Toast.makeText(
                this,
                "Unable to open file link.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateStatus(newStatus: String) {
        val status = normalizeStatus(newStatus)

        btnUpdateStatus.isEnabled = false
        btnUpdateStatus.text = "Saving..."

        RetrofitClient.instance.updateStaffOrderStatus(
            orderId,
            UpdateStaffOrderStatusRequest(status)
        ).enqueue(object : Callback<StaffOrderDetailsResponse> {
            override fun onResponse(
                call: Call<StaffOrderDetailsResponse>,
                response: Response<StaffOrderDetailsResponse>
            ) {
                btnUpdateStatus.isEnabled = true
                btnUpdateStatus.text = "Save Status"

                if (response.isSuccessful && response.body() != null) {
                    Toast.makeText(
                        this@StaffViewOrderActivity,
                        "Order status updated.",
                        Toast.LENGTH_SHORT
                    ).show()

                    renderOrder(response.body()!!)
                } else {
                    Toast.makeText(
                        this@StaffViewOrderActivity,
                        "Failed to update status.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<StaffOrderDetailsResponse>, t: Throwable) {
                btnUpdateStatus.isEnabled = true
                btnUpdateStatus.text = "Save Status"

                Toast.makeText(
                    this@StaffViewOrderActivity,
                    "Update error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun normalizeStatus(value: String): String {
        return when (value.trim().lowercase()) {
            "ready" -> "Ready for Pickup"
            "ready for pickup" -> "Ready for Pickup"
            "completed" -> "Completed"
            "printing" -> "Printing"
            else -> "Pending"
        }
    }

    private fun applyStatusChipColor(status: String) {
        when (status) {
            "Pending" -> {
                tvStatusChip.setBackgroundResource(R.drawable.bg_status_pending)
                tvStatusChip.setTextColor(getColorCompat("#A06A00"))
            }

            "Printing" -> {
                tvStatusChip.setBackgroundResource(R.drawable.bg_status_printing)
                tvStatusChip.setTextColor(getColorCompat("#2563EB"))
            }

            "Ready for Pickup" -> {
                tvStatusChip.setBackgroundResource(R.drawable.bg_status_ready)
                tvStatusChip.setTextColor(getColorCompat("#15803D"))
            }

            "Completed" -> {
                tvStatusChip.setBackgroundResource(R.drawable.bg_status_completed)
                tvStatusChip.setTextColor(getColorCompat("#475467"))
            }
        }
    }

    private fun applyProgressColor(status: String) {
        resetProgress()

        when (status) {
            "Pending" -> {
                stepPendingIcon.setBackgroundResource(R.drawable.bg_progress_pending)
                stepPendingText.setTextColor(getColorCompat("#A06A00"))
            }

            "Printing" -> {
                stepPendingIcon.setBackgroundResource(R.drawable.bg_progress_completed)
                stepPrintingIcon.setBackgroundResource(R.drawable.bg_progress_printing)

                stepPendingText.setTextColor(getColorCompat("#475467"))
                stepPrintingText.setTextColor(getColorCompat("#2563EB"))
            }

            "Ready for Pickup" -> {
                stepPendingIcon.setBackgroundResource(R.drawable.bg_progress_completed)
                stepPrintingIcon.setBackgroundResource(R.drawable.bg_progress_completed)
                stepReadyIcon.setBackgroundResource(R.drawable.bg_progress_ready)

                stepPendingText.setTextColor(getColorCompat("#475467"))
                stepPrintingText.setTextColor(getColorCompat("#475467"))
                stepReadyText.setTextColor(getColorCompat("#15803D"))
            }

            "Completed" -> {
                stepPendingIcon.setBackgroundResource(R.drawable.bg_progress_completed)
                stepPrintingIcon.setBackgroundResource(R.drawable.bg_progress_completed)
                stepReadyIcon.setBackgroundResource(R.drawable.bg_progress_completed)
                stepCompletedIcon.setBackgroundResource(R.drawable.bg_progress_completed)

                stepPendingText.setTextColor(getColorCompat("#475467"))
                stepPrintingText.setTextColor(getColorCompat("#475467"))
                stepReadyText.setTextColor(getColorCompat("#475467"))
                stepCompletedText.setTextColor(getColorCompat("#475467"))
            }
        }
    }

    private fun resetProgress() {
        stepPendingIcon.setBackgroundResource(R.drawable.bg_progress_inactive)
        stepPrintingIcon.setBackgroundResource(R.drawable.bg_progress_inactive)
        stepReadyIcon.setBackgroundResource(R.drawable.bg_progress_inactive)
        stepCompletedIcon.setBackgroundResource(R.drawable.bg_progress_inactive)

        stepPendingText.setTextColor(getColorCompat("#667085"))
        stepPrintingText.setTextColor(getColorCompat("#667085"))
        stepReadyText.setTextColor(getColorCompat("#667085"))
        stepCompletedText.setTextColor(getColorCompat("#667085"))
    }

    private fun getColorCompat(hex: String): Int {
        return Color.parseColor(hex)
    }
}
