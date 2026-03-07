package com.fraternity.reimbursement.dto

import com.fraternity.reimbursement.model.ReimbursementCategory

data class SubmitReimbursementRequest(
    val fullName: String,
    val message: String,
    val category: ReimbursementCategory,
    val amount: Double,
    val paymentMethod: String
)