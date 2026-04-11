package com.printit.mobile.model

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String,
    val studentId: String? = null,
    val staffId: String? = null
)