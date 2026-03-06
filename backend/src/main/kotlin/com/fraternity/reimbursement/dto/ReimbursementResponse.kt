package com.fraternity.reimbursement.dto

data class ReimbursementResponse(
    val id: String,
    val fullName: String,
    val message: String,
    val category: String,
    val receiptUrl: String,
    val status: String,
    val receiptEstablishment: String? = null,
    val receiptTotal: Double? = null,
    val receiptDate: String? = null,
    val createdAt: String
)
