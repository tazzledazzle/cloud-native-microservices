package com.cloudnative.service

import com.cloudnative.common.events.NotificationSentEvent
import com.cloudnative.common.events.OrderProcessedEvent
import com.cloudnative.common.events.UserCreatedEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest : BaseServiceTest<NotificationSentEvent>() {

    @InjectMocks
    lateinit var notificationService: NotificationService

    @Test
    fun `should handle user created event and publish notification sent event`() {
        val userEvent = UserCreatedEvent(
            eventType = "user.created",
            userId = "1",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        notificationService.handleUserCreated(userEvent)

        val captor = ArgumentCaptor.forClass(NotificationSentEvent::class.java)
        Mockito.verify(kafkaTemplate).send(Mockito.eq("notification-events"), captor.capture())

        val sent = captor.value
        assert(sent.eventType == "notification.sent")
        assert(sent.recipientEmail == "test@example.com")
        assert(sent.subject == "Welcome to Our Platform!")
    }

    @Test
    fun `should handle order processed event and publish notification sent event`() {
        val orderEvent = OrderProcessedEvent(
            eventType = "order.processed",
            orderId = "1",
            userId = "1",
            totalAmount = 100.0,
            status = "PENDING",
            createdAt = LocalDateTime.now()
        )

        notificationService.handleOrderProcessed(orderEvent)

        val captor = ArgumentCaptor.forClass(NotificationSentEvent::class.java)
        Mockito.verify(kafkaTemplate).send(Mockito.eq("notification-events"), captor.capture())

        val sent = captor.value
        assert(sent.eventType == "notification.sent")
        assert(sent.subject == "Order Confirmation")
    }
}
