package com.fraternity.reimbursement.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@ConfigurationProperties(prefix = "r2")
data class R2Properties(
    val bucketName: String,
    val endpoint: String,
    val credentials: R2Credentials
) {
    data class R2Credentials(
        val accessKey: String,
        val secretKey: String
    )
}

@Configuration
class R2Config {

    @Bean
    fun s3Client(props: R2Properties): S3Client =
        S3Client.builder()
            .endpointOverride(URI.create(props.endpoint))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(props.credentials.accessKey, props.credentials.secretKey)
                )
            )
            .region(Region.of("auto"))
            .forcePathStyle(true)
            .build()
}
