package com.cloudnative.common.events

import kotlinx.datetime.LocalDateTime

data class UserCreatedEvent(
    val eventType: String,
    val userId: String,
    val name: String,
    val email: String,
    val createdAt: LocalDateTime
)
