package com.cloudnative.service

import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.common.events.OrderProcessedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime

@Service
open class NotificationService(
    override val kafkaTemplate: KafkaTemplate<String, Any>
) : BaseService(kafkaTemplate) {
    
    @KafkaListener(topics = ["user-events"], groupId = "notification-service")
    fun handleUserCreated(event: UserCreatedEvent) {
        // Send welcome email
        sendNotification(
            email = event.email,
            subject = "Welcome to Our Platform!",
            message = "Welcome ${event.firstName} ${event.lastName}! Your account has been created successfully."
        )
    }
    
    @KafkaListener(topics = ["order-events"], groupId = "notification-service")
    fun handleOrderProcessed(event: OrderProcessedEvent) {
        // Send order confirmation email
        sendNotification(
            email = "${event.userId}@example.com", // In real implementation, we'd get the email from user service
            subject = "Order Confirmation",
            message = "Your order #${event.orderId} has been processed successfully. Total amount: $${event.totalAmount}"
        )
    }
    
    open fun sendNotification(email: String, subject: String, message: String) {
        // In real implementation, this would integrate with an email service
        println("[Notification] Sending email to $email: $subject - $message")
        
        // Publish notification.sent event
        publishEvent("notification-events", UserCreatedEvent(
            eventType = "notification.sent",
            userId = "1",
            firstName = "Notification",
            lastName = "Service",
            email = email,
            createdAt = LocalDateTime.now()
        ))
    }
        println("[Notification] Sending email to $email: $subject - $message")
    }
}
