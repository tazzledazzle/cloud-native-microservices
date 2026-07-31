package com.cloudnative.common.events

import java.time.LocalDateTime

data class NotificationSentEvent(
    val eventType: String = "notification.sent",
    val recipientEmail: String,
    val subject: String,
    val sentAt: LocalDateTime
)
