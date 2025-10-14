package com.aifraud.transaction

import com.aifraud.transaction.model.TransactionRequest
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import com.aifraud.transaction.persistence.Transaction
import com.aifraud.transaction.persistence.TransactionRepository
import java.math.BigDecimal
import java.time.OffsetDateTime

@SpringBootTest(
    properties = [
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.format_sql=true",
        "spring.jpa.properties.hibernate.jdbc.time_zone=UTC",
        "spring.flyway.locations=classpath:db/migration",
        "spring.kafka.bootstrap-servers=localhost:9092"
    ]
)
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var transactionRepository: TransactionRepository

    @MockBean
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    @BeforeEach
    fun cleanDatabase() {
        transactionRepository.deleteAll()
    }

    @Test
    fun `should accept transaction`() {
        val payload = TransactionRequest(
            transactionId = "txn-123",
            accountId = "acc-456",
            amount = BigDecimal("250.00"),
            currency = "USD",
            merchantCategory = "ELECTRONICS",
            channel = "WEB",
            deviceId = "device-abc",
            metadata = mapOf("ip" to "127.0.0.1")
        )

        val result = mockMvc.post("/api/v1/transactions") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(payload)
        }.andReturn()

        assertThat(result.response.status).isEqualTo(202)
        assertThat(result.response.contentAsString).contains("\"ACCEPTED\"")

        val saved = transactionRepository.findById("txn-123")
        assertThat(saved).isPresent
        assertThat(saved.get().metadata["ip"]).isEqualTo("127.0.0.1")

        verify(kafkaTemplate).send(eq("transactions.raw"), eq("txn-123"), anyString())
    }

    @Test
    fun `should fetch transaction by id`() {
        val entity = Transaction(
            id = "txn-999",
            accountId = "acct-100",
            amount = BigDecimal("100.00"),
            currency = "USD",
            merchantCategory = "GROCERY",
            channel = "MOBILE",
            deviceId = "device-1",
            metadata = mapOf("ip" to "10.0.0.1"),
            createdAt = OffsetDateTime.parse("2024-01-01T12:00:00Z")
        )
        transactionRepository.save(entity)

        mockMvc.get("/api/v1/transactions/{id}", "txn-999")
            .andExpect {
                status { isOk() }
                jsonPath("$.transactionId", equalTo("txn-999"))
                jsonPath("$.metadata.ip", equalTo("10.0.0.1"))
            }
    }

    @Test
    fun `should list transactions with optional account filter`() {
        (1..3).forEach { index ->
            transactionRepository.save(
                Transaction(
                    id = "txn-$index",
                    accountId = if (index % 2 == 0) "acct-1" else "acct-2",
                    amount = BigDecimal.valueOf(50L * index),
                    currency = "USD",
                    merchantCategory = "RETAIL",
                    channel = null,
                    deviceId = null,
                    metadata = emptyMap(),
                    createdAt = OffsetDateTime.parse("2024-01-01T0${index}:00:00Z")
                )
            )
        }

        mockMvc.get("/api/v1/transactions")
            .andExpect {
                status { isOk() }
                jsonPath("$.content.length()", equalTo(3))
            }

        mockMvc.get("/api/v1/transactions") {
            param("accountId", "acct-1")
        }.andExpect {
            status { isOk() }
            jsonPath("$.content.length()", equalTo(1))
            jsonPath("$.content[0].accountId", equalTo("acct-1"))
        }
    }
}
