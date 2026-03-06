package com.fraternity.reimbursement.service

import com.fraternity.reimbursement.dto.ReceiptValidationResult
import com.fraternity.reimbursement.dto.ReimbursementResponse
import com.fraternity.reimbursement.dto.SubmitReimbursementRequest
import com.fraternity.reimbursement.model.Reimbursement
import com.fraternity.reimbursement.repository.ReimbursementRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ReimbursementService(
    private val r2Service: R2Service,
    private val tabscannerService: TabscannerService,
    private val reimbursementRepository: ReimbursementRepository
) {

    fun submitReimbursement(
        request: SubmitReimbursementRequest,
        receiptFile: MultipartFile
    ): ReimbursementResponse {
        val upload = r2Service.uploadReceipt(receiptFile)

        val validation = try {
            tabscannerService.processReceipt(receiptFile)
        } catch (e: TabscannerException) {
            null
        }

        val reimbursement = reimbursementRepository.save(
            Reimbursement(
                fullName = request.fullName,
                message = request.message,
                category = request.category,
                receiptUrl = upload.url,
                receiptKey = upload.key,
                receiptEstablishment = validation?.establishment,
                receiptDate = validation?.date,
                receiptTotal = validation?.total,
                receiptCurrency = validation?.currency
            )
        )

        return toResponse(reimbursement, validation)
    }

    fun getAllSubmissions(): List<ReimbursementResponse> =
        reimbursementRepository.findAll().map { toResponse(it, null) }

    private fun toResponse(r: Reimbursement, validation: ReceiptValidationResult?): ReimbursementResponse =
        ReimbursementResponse(
            id = r.id.toString(),
            fullName = r.fullName,
            message = r.message,
            category = r.category.name,
            receiptUrl = r.receiptUrl,
            status = r.status.name,
            receiptEstablishment = validation?.establishment ?: r.receiptEstablishment,
            receiptTotal = validation?.total ?: r.receiptTotal,
            receiptDate = validation?.date ?: r.receiptDate,
            createdAt = r.createdAt.toString()
        )
}
