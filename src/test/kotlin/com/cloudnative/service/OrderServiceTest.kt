package com.cloudnative.service

import com.cloudnative.model.Order
import com.cloudnative.model.OrderStatus
import com.cloudnative.repository.OrderRepository
import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.common.events.OrderProcessedEvent
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class OrderServiceTest : BaseServiceTest<OrderProcessedEvent>() {

    @Mock
    lateinit var orderRepository: OrderRepository

    @InjectMocks
    lateinit var orderService: OrderService

    @Test
    fun `should handle user created event and create order`() {
        val userEvent = UserCreatedEvent(
            eventType = "user.created",
            userId = "1",
            firstName = "Test",
            lastName = "User",
            email = "test@example.com",
            createdAt = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        )

        val savedOrder = Order(
            id = 1L,
            userId = 1L,
            totalAmount = 100.0,
            status = OrderStatus.PENDING
        )

        Mockito.`when`(orderRepository.save(any(Order::class.java))).thenReturn(savedOrder)

        orderService.handleUserCreated(userEvent)

        Mockito.verify(orderRepository).save(any(Order::class.java))
        Mockito.verify(kafkaTemplate).send(
            "order-events",
            OrderProcessedEvent(
                eventType = "order.processed",
                orderId = "1",
                userId = "1",
                totalAmount = 100.0,
                status = OrderStatus.PENDING.name,
                createdAt = savedOrder.createdAt
            )
        )
    }

    @Test
    fun `should get orders by user`() {
        val order = Order(id = 1L, userId = 1L, totalAmount = 100.0, status = OrderStatus.PENDING)

        Mockito.`when`(orderRepository.findByUserId(1L)).thenReturn(listOf(order))

        val result = orderService.getOrdersByUser(1L)

        Mockito.verify(orderRepository).findByUserId(1L)
        assert(result == listOf(order))
    }
}
