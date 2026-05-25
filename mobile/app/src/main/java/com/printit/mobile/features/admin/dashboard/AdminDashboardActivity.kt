package com.printit.mobile.features.admin.dashboard

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.printit.mobile.R
import com.printit.mobile.core.network.RetrofitClient
import com.printit.mobile.features.admin.orders.AdminOrdersActivity
import com.printit.mobile.features.admin.payments.AdminPaymentsActivity
import com.printit.mobile.features.admin.users.AdminUsersActivity
import com.printit.mobile.shared.components.MobileTopbarHelper
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var tvProfileBadge: TextView
    private lateinit var tvNotificationBell: TextView
    private lateinit var tvWelcomeTitle: TextView
    private lateinit var tvWelcomeMessage: TextView

    private lateinit var tvTotalUsers: TextView
    private lateinit var tvStudents: TextView
    private lateinit var tvApprovedStaff: TextView
    private lateinit var tvPendingStaff: TextView
    private lateinit var tvPendingStaffMessage: TextView
    private lateinit var pendingStaffContainer: LinearLayout

    private lateinit var navAdminUsers: TextView
    private lateinit var navAdminPayments: TextView
    private lateinit var navAdminOrders: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("printit_prefs", MODE_PRIVATE)

        setContentView(R.layout.activity_admin_dashboard)

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

        tvTotalUsers = findViewById(R.id.tvTotalUsers)
        tvStudents = findViewById(R.id.tvStudents)
        tvApprovedStaff = findViewById(R.id.tvApprovedStaff)
        tvPendingStaff = findViewById(R.id.tvPendingStaff)
        tvPendingStaffMessage = findViewById(R.id.tvPendingStaffMessage)
        pendingStaffContainer = findViewById(R.id.pendingStaffContainer)

        navAdminUsers = findViewById(R.id.navAdminUsers)
        navAdminPayments = findViewById(R.id.navAdminPayments)
        navAdminOrders = findViewById(R.id.navAdminOrders)
    }

    private fun setupUserHeader() {
        MobileTopbarHelper.setup(this, tvProfileBadge, tvNotificationBell)

        tvWelcomeTitle.text = "Admin Dashboard"
        tvWelcomeMessage.text = "Monitor users and approve staff registration requests."
    }

    private fun setupButtons() {
        navAdminUsers.setOnClickListener {
            startActivity(Intent(this, AdminUsersActivity::class.java))
        }

        navAdminPayments.setOnClickListener {
            startActivity(Intent(this, AdminPaymentsActivity::class.java))
        }

        navAdminOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }
    }

    private fun loadDashboard() {
        setLoadingState()

        RetrofitClient.instance.getAdminDashboard()
            .enqueue(object : Callback<AdminDashboardResponse> {
                override fun onResponse(
                    call: Call<AdminDashboardResponse>,
                    response: Response<AdminDashboardResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        renderDashboard(response.body()!!)
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Failed to load admin dashboard.",
                            Toast.LENGTH_SHORT
                        ).show()
                        renderEmptyState()
                    }
                }

                override fun onFailure(call: Call<AdminDashboardResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        "Dashboard error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                    renderEmptyState()
                }
            })
    }

    private fun setLoadingState() {
        tvTotalUsers.text = "..."
        tvStudents.text = "..."
        tvApprovedStaff.text = "..."
        tvPendingStaff.text = "..."
        tvPendingStaffMessage.text = "Loading pending staff requests..."
        pendingStaffContainer.removeAllViews()
    }

    private fun renderDashboard(data: AdminDashboardResponse) {
        tvTotalUsers.text = (data.totalUsers ?: 0L).toString()
        tvStudents.text = (data.totalStudents ?: 0L).toString()
        tvApprovedStaff.text = (data.approvedStaff ?: 0L).toString()
        tvPendingStaff.text = (data.pendingStaff ?: 0L).toString()

        renderPendingStaff(data.pendingStaffRequests.orEmpty())
    }

    private fun renderPendingStaff(requests: List<PendingStaffResponse>) {
        pendingStaffContainer.removeAllViews()

        if (requests.isEmpty()) {
            tvPendingStaffMessage.text = "No pending staff requests."
            return
        }

        tvPendingStaffMessage.text = "Showing ${requests.size} pending request(s)."

        requests.forEach { staff ->
            val card: View = layoutInflater.inflate(
                R.layout.item_pending_staff_request_card,
                pendingStaffContainer,
                false
            )

            val tvStaffName: TextView = card.findViewById(R.id.tvStaffName)
            val tvStaffEmail: TextView = card.findViewById(R.id.tvStaffEmail)
            val tvStaffId: TextView = card.findViewById(R.id.tvStaffId)
            val btnApproveStaff: TextView = card.findViewById(R.id.btnApproveStaff)
            val btnRejectStaff: TextView = card.findViewById(R.id.btnRejectStaff)

            tvStaffName.text = staff.fullName ?: "Unnamed Staff"
            tvStaffEmail.text = staff.email ?: "-"
            tvStaffId.text = "Staff ID: ${staff.staffId ?: "Not set"}"

            btnApproveStaff.setOnClickListener {
                val staffId = staff.id

                if (staffId == null) {
                    Toast.makeText(this, "Invalid staff request.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                approveStaff(staffId)
            }

            btnRejectStaff.setOnClickListener {
                val staffId = staff.id

                if (staffId == null) {
                    Toast.makeText(this, "Invalid staff request.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                rejectStaff(staffId)
            }

            pendingStaffContainer.addView(card)
        }
    }

    private fun approveStaff(userId: Long) {
        RetrofitClient.instance.approveStaffRequest(userId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Staff approved successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadDashboard()
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Failed to approve staff.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        "Approve error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun rejectStaff(userId: Long) {
        RetrofitClient.instance.rejectStaffRequest(userId)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Staff rejected successfully.",
                            Toast.LENGTH_SHORT
                        ).show()

                        loadDashboard()
                    } else {
                        Toast.makeText(
                            this@AdminDashboardActivity,
                            "Failed to reject staff.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(
                        this@AdminDashboardActivity,
                        "Reject error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    private fun renderEmptyState() {
        tvTotalUsers.text = "0"
        tvStudents.text = "0"
        tvApprovedStaff.text = "0"
        tvPendingStaff.text = "0"
        tvPendingStaffMessage.text = "No pending staff requests."
        pendingStaffContainer.removeAllViews()
    }
}