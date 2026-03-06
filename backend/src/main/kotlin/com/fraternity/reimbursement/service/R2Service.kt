package com.fraternity.reimbursement.service

import com.fraternity.reimbursement.config.R2Properties
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.util.UUID

@Service
class R2Service(
    private val s3Client: S3Client,
    private val r2Props: R2Properties
) {

    fun uploadReceipt(file: MultipartFile): UploadResult {
        val extension = file.originalFilename?.substringAfterLast('.', "jpg") ?: "jpg"
        val key = "receipts/${UUID.randomUUID()}.$extension"

        val putRequest = PutObjectRequest.builder()
            .bucket(r2Props.bucketName)
            .key(key)
            .contentType(file.contentType ?: "image/jpeg")
            .build()

        s3Client.putObject(putRequest, RequestBody.fromBytes(file.bytes))

        val url = "${r2Props.endpoint}/${r2Props.bucketName}/$key"
        return UploadResult(key = key, url = url)
    }
}

data class UploadResult(
    val key: String,
    val url: String
)
