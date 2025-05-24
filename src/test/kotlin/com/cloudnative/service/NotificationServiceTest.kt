package com.cloudnative.service

import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.common.events.OrderProcessedEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest : BaseServiceTest() {

    @InjectMocks
    lateinit var notificationService: NotificationService

    @Test
    fun `should handle user created event and send notification`() {
        val userEvent = UserCreatedEvent(
            eventType = "user.created",
            userId = "1",
            email = "test@example.com",
            createdAt = LocalDateTime.now()
        )

        notificationService.handleUserCreated(userEvent)

        Mockito.verify(notificationService).sendNotification(
            "test@example.com",
            "Welcome to Our Platform!",
            "Welcome! Your account has been created successfully."
        )
        Mockito.verify(kafkaTemplate).send(
            "notification-events",
            UserCreatedEvent(
                eventType = "notification.sent",
                userId = "1",
                email = "test@example.com",
                createdAt = LocalDateTime.now()
            )
        )
    }

    @Test
    fun `should handle order processed event and send notification`() {
        val orderEvent = OrderProcessedEvent(
            eventType = "order.processed",
            orderId = "1",
            userId = "1",
            totalAmount = 100.0,
            status = "PENDING",
            createdAt = LocalDateTime.now()
        )

        notificationService.handleOrderProcessed(orderEvent)

        Mockito.verify(notificationService).sendNotification(
            "1@example.com",
            "Order Confirmation",
            "Your order #1 has been processed successfully. Total amount: $100.0"
        )
    }
}
