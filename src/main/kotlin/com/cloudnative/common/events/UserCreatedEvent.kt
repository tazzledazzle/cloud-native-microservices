package com.cloudnative.common.events

import java.time.LocalDateTime

data class UserCreatedEvent(
    val eventType: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val createdAt: LocalDateTime
)
