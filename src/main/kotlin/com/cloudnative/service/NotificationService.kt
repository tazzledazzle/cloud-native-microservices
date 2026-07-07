package com.cloudnative.service

import com.cloudnative.common.events.NotificationSentEvent
import com.cloudnative.common.events.OrderProcessedEvent
import com.cloudnative.common.events.UserCreatedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
open class NotificationService(
    override val kafkaTemplate: KafkaTemplate<String, NotificationSentEvent>
) : BaseService<NotificationSentEvent>(kafkaTemplate) {

    @KafkaListener(topics = ["user-events"], groupId = "notification-service")
    fun handleUserCreated(event: UserCreatedEvent) {
        sendNotification(
            email = event.email,
            subject = "Welcome to Our Platform!",
            message = "Welcome ${event.firstName} ${event.lastName}! Your account has been created successfully."
        )
    }

    @KafkaListener(topics = ["order-events"], groupId = "notification-service")
    fun handleOrderProcessed(event: OrderProcessedEvent) {
        sendNotification(
            email = "${event.userId}@example.com",
            subject = "Order Confirmation",
            message = "Your order #${event.orderId} has been processed successfully. Total: $${event.totalAmount}"
        )
    }

    open fun sendNotification(email: String, subject: String, message: String) {
        println("[Notification] Sending email to $email: $subject - $message")
        publishEvent("notification-events", NotificationSentEvent(
            recipientEmail = email,
            subject = subject,
            sentAt = LocalDateTime.now()
        ))
    }
}
