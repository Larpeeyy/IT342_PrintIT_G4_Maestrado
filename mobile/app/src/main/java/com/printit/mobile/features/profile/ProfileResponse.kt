package com.printit.mobile.features.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
    @SerializedName("id")
    val id: Long?,

    @SerializedName("fullName")
    val fullName: String?,

    @SerializedName("username")
    val username: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("role")
    val role: String?,

    @SerializedName("studentId")
    val studentId: String?,

    @SerializedName("staffId")
    val staffId: String?,

    @SerializedName("profileImageUrl")
    val profileImageUrl: String?
)