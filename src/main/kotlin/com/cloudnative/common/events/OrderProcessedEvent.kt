package com.cloudnative.common.events

import java.time.LocalDateTime

data class OrderProcessedEvent(
    val eventType: String,
    val orderId: String,
    val userId: String,
    val totalAmount: Double,
    val status: String,
    val createdAt: LocalDateTime
)
