package com.aifraud.fraud.client

import com.aifraud.fraud.config.RiskScoringProperties
import com.aifraud.fraud.messaging.TransactionEvent
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Component
@Primary
@ConditionalOnProperty(prefix = "fraud.scoring.http", name = ["enabled"], havingValue = "true")
class HttpRiskScoringClient(
    private val webClient: WebClient,
    private val properties: RiskScoringProperties
) : RiskScoringClient {

    private val logger = LoggerFactory.getLogger(HttpRiskScoringClient::class.java)

    override fun score(event: TransactionEvent): RiskScore {
        logger.debug("Requesting remote risk score for transaction {}", event.transactionId)
        val response = webClient.post()
            .uri("/v1/score")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(event)
            .retrieve()
            .bodyToMono(RiskScoreResponse::class.java)
            .timeout(properties.timeout)
            .onErrorResume { throwable ->
                logger.warn("Remote risk scoring failed, falling back to default heuristics", throwable)
                Mono.empty()
            }
            .block()

        return if (response != null) {
            RiskScore(value = response.score, rationale = response.rationale)
        } else {
            FALLBACK.score(event)
        }
    }

    companion object {
        private val FALLBACK = DefaultRiskScoringClient()
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
private data class RiskScoreResponse(
    val score: Double,
    val rationale: String
)
