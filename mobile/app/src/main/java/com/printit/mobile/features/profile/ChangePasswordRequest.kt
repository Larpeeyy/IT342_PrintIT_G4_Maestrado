package com.printit.mobile.features.profile

data class ChangePasswordRequest(
    val email: String,
    val currentPassword: String,
    val newPassword: String
)