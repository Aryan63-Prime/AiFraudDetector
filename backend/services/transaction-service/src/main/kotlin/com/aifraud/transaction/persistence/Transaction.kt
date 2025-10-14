package com.aifraud.transaction.persistence

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Converter
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.OffsetDateTime

@Entity
@Table(name = "transactions")
data class Transaction(
    @Id
    @Column(name = "transaction_id", nullable = false, length = 64)
    val id: String,

    @Column(name = "account_id", nullable = false, length = 64)
    val accountId: String,

    @Column(name = "amount", nullable = false, precision = 19, scale = 4)
    val amount: BigDecimal,

    @Column(name = "currency", nullable = false, length = 3)
    val currency: String,

    @Column(name = "merchant_category", nullable = false, length = 64)
    val merchantCategory: String,

    @Column(name = "channel", length = 32)
    val channel: String? = null,

    @Column(name = "device_id", length = 64)
    val deviceId: String? = null,

    @Convert(converter = TransactionMetadataConverter::class)
    @Column(name = "metadata", columnDefinition = "TEXT")
    val metadata: Map<String, Any?> = emptyMap(),

    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime
)

@Converter(autoApply = true)
class TransactionMetadataConverter : AttributeConverter<Map<String, Any?>, String> {

    private val objectMapper = jacksonObjectMapper()

    override fun convertToDatabaseColumn(attribute: Map<String, Any?>?): String {
        return objectMapper.writeValueAsString(attribute ?: emptyMap<String, Any?>())
    }

    override fun convertToEntityAttribute(dbData: String?): Map<String, Any?> {
        if (dbData.isNullOrBlank()) {
            return emptyMap()
        }
        return objectMapper.readValue(dbData, object : TypeReference<Map<String, Any?>>() {})
    }
}
