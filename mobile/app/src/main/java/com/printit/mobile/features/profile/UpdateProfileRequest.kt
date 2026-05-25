package com.printit.mobile.features.profile

data class UpdateProfileRequest(
    val email: String,
    val fullName: String,
    val username: String,
    val profileImageUrl: String?
)