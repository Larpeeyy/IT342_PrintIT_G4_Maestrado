package com.printit.mobile.features.admin.orders

import com.google.gson.annotations.SerializedName

data class AdminOrderResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("fileName")
    val fileName: String?,

    @SerializedName("studentName")
    val studentName: String?,

    @SerializedName("dateSubmitted")
    val dateSubmitted: String?,

    @SerializedName("copies")
    val copies: Long?,

    @SerializedName("status")
    val status: String?
)