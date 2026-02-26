package com.fraternity.reimbursement.controller

import com.fraternity.reimbursement.model.ReimbursementCategory
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
class ReceiptController {

    @GetMapping("/form")
    fun getSubmissionForm(): ResponseEntity<Map<String, Any>> {
        return ResponseEntity.ok(mapOf(
            "categories" to ReimbursementCategory.entries.map { it.name }
        ))
    }

    @PostMapping
    fun submitReimbursement(
        @RequestPart("receipt") receipt: MultipartFile,
        @Valid @RequestPart("request") request: SubmitReimbursementRequest
        ): ResponseEntity<Map<String, String>>  // TODO: SubmitReimbursementRequest in dto (data transfer object)  
        {
        // TODO: upload receipt to S3, save submission to DB
        return ResponseEntity.ok(mapOf("status" to "received"))
    }

    @GetMapping()
    fun getMySubmissions(): ResponseEntity<List<ReimbursementResponse>> {
        return ResponseEntity.ok(emptyList())
    }
}
