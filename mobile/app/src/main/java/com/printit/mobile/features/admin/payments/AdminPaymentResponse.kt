package com.printit.mobile.features.admin.payments

import com.google.gson.annotations.SerializedName

data class AdminPaymentResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("paymentCode")
    val paymentCode: String?,

    @SerializedName("orderCode")
    val orderCode: String?,

    @SerializedName("studentName")
    val studentName: String?,

    @SerializedName("fileName")
    val fileName: String?,

    @SerializedName("provider")
    val provider: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("amount")
    val amount: Double?,

    @SerializedName("createdAt")
    val createdAt: String?
)