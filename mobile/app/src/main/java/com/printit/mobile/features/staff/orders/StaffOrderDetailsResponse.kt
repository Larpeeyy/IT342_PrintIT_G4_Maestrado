package com.printit.mobile.features.staff.orders

import com.google.gson.annotations.SerializedName

data class StaffOrderDetailsResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("orderCode")
    val orderCode: String?,

    @SerializedName("studentName")
    val studentName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("fileName")
    val fileName: String?,

    @SerializedName("fileUrl")
    val fileUrl: String?,

    @SerializedName("paperSize")
    val paperSize: String?,

    @SerializedName("colorMode")
    val colorMode: String?,

    @SerializedName("copies")
    val copies: Long?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("totalAmount")
    val totalAmount: Double?,

    @SerializedName("createdAt")
    val createdAt: String?
)