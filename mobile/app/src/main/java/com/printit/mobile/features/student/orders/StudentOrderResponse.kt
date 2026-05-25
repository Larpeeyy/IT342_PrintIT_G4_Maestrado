package com.printit.mobile.features.student.orders

data class StudentOrderResponse(
    val id: Long?,
    val orderCode: String?,
    val fileName: String?,
    val paperSize: String?,
    val colorMode: String?,
    val copies: Int?,
    val status: String?,
    val totalAmount: Double?,
    val createdAt: String?
)