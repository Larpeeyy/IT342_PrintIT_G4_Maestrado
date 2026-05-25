package com.printit.mobile.features.notifications

import com.google.gson.annotations.SerializedName

data class UnreadNotificationCountResponse(
    @SerializedName("count")
    val count: Long?
)