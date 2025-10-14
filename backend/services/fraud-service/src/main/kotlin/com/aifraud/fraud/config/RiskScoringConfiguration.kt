package com.aifraud.fraud.config

import com.aifraud.fraud.client.DefaultRiskScoringClient
import com.aifraud.fraud.client.RiskScoringClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(RiskScoringProperties::class)
class RiskScoringConfiguration {

    @Bean
    @ConditionalOnMissingBean(RiskScoringClient::class)
    fun defaultRiskScoringClient(): RiskScoringClient = DefaultRiskScoringClient()

    @Bean
    fun riskScoringWebClient(properties: RiskScoringProperties): WebClient {
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { configurer ->
                configurer.defaultCodecs().maxInMemorySize(1 * 1024 * 1024)
            }
            .build()

        return WebClient.builder()
            .baseUrl(properties.baseUrl.trimEnd('/'))
            .exchangeStrategies(exchangeStrategies)
            .build()
    }
}
