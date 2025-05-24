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
class NotificationServiceTest : BaseServiceTest<UserCreatedEvent>() {

    @InjectMocks
    lateinit var notificationService: NotificationService

    @Test
    fun `should handle user created event and send notification`() {
        val userEvent = UserCreatedEvent(
            eventType = "user.created",
            userId = "1",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            createdAt = LocalDateTime.now()
        )

        notificationService.handleUserCreated(userEvent)

        Mockito.verify(kafkaTemplate).send(
            "notification-events",
            UserCreatedEvent(
                eventType = "notification.sent",
                userId = "1",
                firstName = "Notification",
                lastName = "Service",
                email = "test@example.com",
                createdAt = Mockito.any()
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

        Mockito.verify(kafkaTemplate).send(
            "notification-events",
            UserCreatedEvent(
                eventType = "notification.sent",
                userId = "1",
                firstName = "Notification",
                lastName = "Service",
                email = "1@example.com",
                createdAt = Mockito.any()
            )
        )
    }
}
