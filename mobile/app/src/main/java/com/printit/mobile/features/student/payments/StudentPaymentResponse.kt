package com.printit.mobile.features.student.payments

data class StudentPaymentResponse(
    val id: Long?,
    val paymentCode: String?,
    val orderCode: String?,
    val fileName: String?,
    val provider: String?,
    val status: String?,
    val amount: Double?,
    val createdAt: String?
)