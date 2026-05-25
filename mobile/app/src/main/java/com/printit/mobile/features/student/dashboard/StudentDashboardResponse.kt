package com.printit.mobile.features.student.dashboard

import com.google.gson.annotations.SerializedName

data class StudentDashboardResponse(
    @SerializedName("totalOrders")
    val totalOrders: Long?,

    @SerializedName("pendingOrders")
    val pendingOrders: Long?,

    @SerializedName("readyForPickup")
    val readyForPickup: Long?,

    @SerializedName("readyForPickupOrders")
    val readyForPickupOrders: Long?,

    @SerializedName("totalSpent")
    val totalSpent: Double?,

    @SerializedName("recentOrders")
    val recentOrders: List<StudentRecentOrderResponse>?
)

data class StudentRecentOrderResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("orderCode")
    val orderCode: String?,

    @SerializedName("fileName")
    val fileName: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("totalAmount")
    val totalAmount: Double?,

    @SerializedName("dateSubmitted")
    val dateSubmitted: String?,

    @SerializedName("createdAt")
    val createdAt: String?
)