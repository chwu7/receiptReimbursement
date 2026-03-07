package com.fraternity.reimbursement.service

import com.fraternity.reimbursement.config.TabscannerProperties
import com.fraternity.reimbursement.dto.ReceiptValidationResult
import com.fraternity.reimbursement.dto.TabscannerProcessResponse
import com.fraternity.reimbursement.dto.TabscannerResultResponse
import org.slf4j.LoggerFactory
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.multipart.MultipartFile

@Service
class TabscannerService(
    private val tabscannerRestClient: RestClient,
    private val props: TabscannerProperties
) {
    private val log = LoggerFactory.getLogger(TabscannerService::class.java)

    fun processReceipt(file: MultipartFile): ReceiptValidationResult {
        val token = uploadReceipt(file)
        return pollForResult(token)
    }

    private fun uploadReceipt(file: MultipartFile): String {
        val fileResource = object : ByteArrayResource(file.bytes) {
            override fun getFilename(): String = file.originalFilename ?: "receipt.jpg"
        }

        val body = LinkedMultiValueMap<String, Any>().apply {
            add("file", fileResource)
            add("documentType", "receipt")
        }

        val response = tabscannerRestClient.post()
            .uri("/process")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(body)
            .retrieve()
            .body(TabscannerProcessResponse::class.java)
            ?: throw TabscannerException("No response from Tabscanner /process endpoint")

        if (!response.success || response.token == null) {
            throw TabscannerException("Tabscanner upload failed: status=${response.status}, code=${response.statusCode}")
        }

        log.info("Tabscanner upload successful, token={}", response.token)
        return response.token
    }

    private fun pollForResult(token: String): ReceiptValidationResult {
        repeat(props.maxPollAttempts) { attempt ->
            Thread.sleep(props.pollIntervalMs)

            val response = tabscannerRestClient.get()
                .uri("https://api.tabscanner.com/api/result/{token}", token)
                .retrieve()
                .body(TabscannerResultResponse::class.java)
                ?: throw TabscannerException("No response from Tabscanner /result endpoint")

            log.info("Poll attempt {} for token={}, status={}, status_code={}", attempt + 1, token, response.status, response.statusCode)

            if (response.result != null) {
                val result = response.result
                return ReceiptValidationResult(
                    establishment = result.establishment,
                    date = result.dateISO ?: result.date,
                    total = result.total,
                    subTotal = result.subTotal,
                    tax = result.tax,
                    currency = result.currency,
                    lineItems = result.lineItems,
                    totalConfidence = result.totalConfidence
                )
            }

            if (response.status == "failed") {
                throw TabscannerException("Tabscanner processing failed: status_code=${response.statusCode}")
            }

            log.debug("Result not yet available, continuing to poll...")
        }
        throw TabscannerException("Tabscanner polling timed out after ${props.maxPollAttempts} attempts")
    }
}

class TabscannerException(message: String) : RuntimeException(message)
