package com.fraternity.reimbursement.service

import com.fraternity.reimbursement.dto.ReceiptValidationResult
import com.fraternity.reimbursement.dto.ReimbursementResponse
import com.fraternity.reimbursement.dto.SubmitReimbursementRequest
import com.fraternity.reimbursement.model.Reimbursement
import com.fraternity.reimbursement.model.ReimbursementStatus
import com.fraternity.reimbursement.repository.ReimbursementRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal

@Service
class ReimbursementService(
    private val r2Service: R2Service,
    private val tabscannerService: TabscannerService,
    private val reimbursementRepository: ReimbursementRepository
) {
    private val log = LoggerFactory.getLogger(ReimbursementService::class.java)

    fun submitReimbursement(
        request: SubmitReimbursementRequest,
        receiptFile: MultipartFile
    ): ReimbursementResponse {
        val upload = r2Service.uploadReceipt(receiptFile)

        val validation = try {
            tabscannerService.processReceipt(receiptFile)
        } catch (e: Exception) {
            log.warn("Tabscanner processing failed, continuing without validation: {}", e.message)
            null
        }

        val status = if (validation?.total != null &&
            BigDecimal.valueOf(validation.total).compareTo(BigDecimal.valueOf(request.amount)) == 0
        ) {
            ReimbursementStatus.VERIFIED
        } else {
            ReimbursementStatus.PENDING
        }

        val reimbursement = reimbursementRepository.save(
            Reimbursement(
                userId = 1, // TODO: replace with authenticated user id
                fullName = request.fullName,
                message = request.message,
                category = request.category,
                amount = BigDecimal.valueOf(request.amount),
                paymentMethod = request.paymentMethod,
                receiptR2Key = upload.key,
                receiptUrl = upload.url,
                receiptKey = upload.key,
                receiptEstablishment = validation?.establishment,
                receiptDate = validation?.date,
                receiptTotal = validation?.total,
                status = status
            )
        )

        return toResponse(reimbursement, validation)
    }

    @Transactional
    fun updateStatus(id: Long, status: ReimbursementStatus): ReimbursementResponse {
        val updated = reimbursementRepository.updateStatus(id, status)
        if (updated == 0) throw IllegalArgumentException("Reimbursement not found: $id")
        val reimbursement = reimbursementRepository.findById(id).get()
        return toResponse(reimbursement, null)
    }

    fun getAllSubmissions(): List<ReimbursementResponse> =
        reimbursementRepository.findAllByOrderByCreatedAtDesc().map { toResponse(it, null) }

    private fun toResponse(r: Reimbursement, validation: ReceiptValidationResult?): ReimbursementResponse =
        ReimbursementResponse(
            id = r.id.toString(),
            formId = r.formId.toString(),
            fullName = r.fullName,
            message = r.message,
            category = r.category.name,
            amount = r.amount.toDouble(),
            paymentMethod = r.paymentMethod,
            receiptUrl = r.receiptUrl,
            status = r.status.name,
            receiptEstablishment = validation?.establishment ?: r.receiptEstablishment,
            receiptTotal = validation?.total ?: r.receiptTotal,
            receiptSubTotal = validation?.subTotal,
            receiptTax = validation?.tax,
            receiptDate = validation?.date ?: r.receiptDate,
            receiptLineItems = validation?.lineItems,
            receiptTotalConfidence = validation?.totalConfidence,
            createdAt = r.createdAt.toString()
        )
}
