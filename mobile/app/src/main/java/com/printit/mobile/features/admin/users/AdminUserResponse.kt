package com.printit.mobile.features.admin.users

import com.google.gson.annotations.SerializedName

data class AdminUserResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("approvalStatus")
    val approvalStatus: String?,

    @SerializedName("username")
    val username: String?
)