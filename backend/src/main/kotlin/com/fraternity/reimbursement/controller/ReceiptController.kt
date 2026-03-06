package com.fraternity.reimbursement.controller

import com.fraternity.reimbursement.dto.ReimbursementResponse
import com.fraternity.reimbursement.dto.SubmitReimbursementRequest
import com.fraternity.reimbursement.model.ReimbursementCategory
import com.fraternity.reimbursement.service.ReimbursementService
import com.fraternity.reimbursement.service.TabscannerException
import com.fraternity.reimbursement.service.TabscannerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import jakarta.validation.Valid


@RestController
@RequestMapping("/api/reimbursements")
class ReceiptController(
    private val tabscannerService: TabscannerService,
    private val reimbursementService: ReimbursementService
) {

    @GetMapping("/form")
    fun getSubmissionForm(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf(
            "categories" to ReimbursementCategory.entries.map { it.name }
        ))
    }

    @PostMapping("/validate-receipt")
    fun validateReceipt(
        @RequestPart("receipt") receipt: MultipartFile
    ): ResponseEntity<Any> {
        return try {
            val result = tabscannerService.processReceipt(receipt)
            ResponseEntity.ok(result)
        } catch (e: TabscannerException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        }
    }

    @PostMapping
    fun submitReimbursement(
        @RequestPart("receipt") receipt: MultipartFile,
        @Valid @RequestPart("request") request: SubmitReimbursementRequest
    ): ResponseEntity<ReimbursementResponse> {
        val response = reimbursementService.submitReimbursement(request, receipt)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getMySubmissions(): ResponseEntity<List<ReimbursementResponse>> {
        return ResponseEntity.ok(reimbursementService.getAllSubmissions())
    }
}
