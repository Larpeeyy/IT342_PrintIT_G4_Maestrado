package com.printit.mobile.model

data class UserResponse(
    val id: Long?,
    val email: String?,
    val fullName: String?,
    val password: String?,
    val role: String?,
    val studentId: String?,
    val staffId: String?
)