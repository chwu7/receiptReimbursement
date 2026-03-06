package com.fraternity.reimbursement.dto

data class ReimbursementResponse(
    val id: String,
    val formId: String,
    val fullName: String,
    val message: String?,
    val category: String,
    val amount: Double,
    val receiptUrl: String,
    val status: String,
    val receiptEstablishment: String? = null,
    val receiptTotal: Double? = null,
    val receiptSubTotal: Double? = null,
    val receiptTax: Double? = null,
    val receiptDate: String? = null,
    val receiptCurrency: String? = null,
    val receiptLineItems: List<TabscannerLineItem>? = null,
    val receiptTotalConfidence: Double? = null,
    val createdAt: String
)
