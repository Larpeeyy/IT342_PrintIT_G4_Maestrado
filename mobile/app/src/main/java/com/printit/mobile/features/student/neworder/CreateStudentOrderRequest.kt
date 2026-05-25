package com.printit.mobile.features.student.neworder

data class CreateStudentOrderRequest(
    val email: String,
    val fileName: String,
    val fileUrl: String?,
    val paperSize: String,
    val colorMode: String,
    val copies: Int
)