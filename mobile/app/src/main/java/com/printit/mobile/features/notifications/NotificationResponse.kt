package com.printit.mobile.features.notifications

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("type")
    val type: String?,

    @SerializedName("isRead")
    val isRead: Boolean?,

    @SerializedName("createdAt")
    val createdAt: String?
)