package com.fraternity.reimbursement.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TabscannerProcessResponse(
    val token: String?,
    val status: String,
    @JsonProperty("status_code") val statusCode: Int,
    val success: Boolean,
    val duplicate: Boolean? = null,
    val duplicateToken: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TabscannerResultResponse(
    val status: String,
    @JsonProperty("status_code") val statusCode: Int,
    val result: TabscannerResult? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TabscannerResult(
    val establishment: String? = null,
    val date: String? = null,
    val dateISO: String? = null,
    val total: Double? = null,
    val subTotal: Double? = null,
    val tax: Double? = null,
    val tip: Double? = null,
    val discount: Double? = null,
    val currency: String? = null,
    val address: String? = null,
    val paymentMethod: String? = null,
    val lineItems: List<TabscannerLineItem>? = null,
    val totalConfidence: Double? = null,
    val dateConfidence: Double? = null,
    val establishmentConfidence: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class TabscannerLineItem(
    val desc: String? = null,
    val descClean: String? = null,
    val lineTotal: Double? = null,
    val price: Double? = null,
    val qty: Double? = null,
    val unit: String? = null
)

data class ReceiptValidationResult(
    val establishment: String?,
    val date: String?,
    val total: Double?,
    val subTotal: Double?,
    val tax: Double?,
    val currency: String?,
    val lineItems: List<TabscannerLineItem>?,
    val totalConfidence: Double?
)
