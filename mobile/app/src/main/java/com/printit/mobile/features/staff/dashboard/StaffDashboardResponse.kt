package com.printit.mobile.features.staff.dashboard

import com.google.gson.annotations.SerializedName

data class StaffDashboardResponse(
    @SerializedName("pendingOrders")
    val pendingOrders: Long?,

    @SerializedName("printingOrders")
    val printingOrders: Long?,

    @SerializedName("readyForPickupOrders")
    val readyForPickupOrders: Long?,

    @SerializedName("completedToday")
    val completedToday: Long?,

    @SerializedName("recentOrders")
    val recentOrders: List<StaffRecentOrderResponse>?
)

data class StaffRecentOrderResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("fileName")
    val fileName: String?,

    @SerializedName("studentName")
    val studentName: String?,

    @SerializedName("copies")
    val copies: Long?,

    @SerializedName("status")
    val status: String?
)