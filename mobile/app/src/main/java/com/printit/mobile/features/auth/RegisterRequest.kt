package com.printit.mobile.features.auth

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String,
    val studentId: String? = null,
    val staffId: String? = null
)