package com.fraternity.reimbursement.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestClient

@ConfigurationProperties(prefix = "app.tabscanner")
data class TabscannerProperties(
    val apiKey: String,
    val baseUrl: String = "https://api.tabscanner.com/api/2",
    val pollIntervalMs: Long = 2000,
    val maxPollAttempts: Int = 15
)

@Configuration
class TabscannerConfig {

    @Bean
    fun tabscannerRestClient(props: TabscannerProperties): RestClient =
        RestClient.builder()
            .baseUrl(props.baseUrl)
            .defaultHeader("apikey", props.apiKey)
            .build()
}
