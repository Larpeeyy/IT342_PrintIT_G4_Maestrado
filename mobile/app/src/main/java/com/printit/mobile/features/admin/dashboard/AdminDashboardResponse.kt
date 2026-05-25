package com.printit.mobile.features.admin.dashboard

import com.google.gson.annotations.SerializedName

data class AdminDashboardResponse(
    @SerializedName("totalUsers")
    val totalUsers: Long?,

    @SerializedName("totalStudents")
    val totalStudents: Long?,

    @SerializedName("approvedStaff")
    val approvedStaff: Long?,

    @SerializedName("pendingStaff")
    val pendingStaff: Long?,

    @SerializedName("pendingStaffRequests")
    val pendingStaffRequests: List<PendingStaffResponse>?
)

data class PendingStaffResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("staffId")
    val staffId: String?,

    @SerializedName("approvalStatus")
    val approvalStatus: String?
)