package com.cloudnative.service

import com.cloudnative.model.Order
import com.cloudnative.model.OrderStatus
import com.cloudnative.repository.OrderRepository
import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.common.events.OrderProcessedEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class OrderService(
    private val orderRepository: OrderRepository,
    private val kafkaTemplate: KafkaTemplate<String, OrderProcessedEvent>
) : BaseService() {
    
    @KafkaListener(topics = ["user-events"], groupId = "order-service")
    fun handleUserCreated(event: UserCreatedEvent) {
        // Create a sample order for the new user
        val order = Order(
            userId = event.userId.toLong(),
            totalAmount = 100.0,
            status = OrderStatus.PENDING
        )
        
        val savedOrder = orderRepository.save(order)
        
        // Publish order.processed event
        publishEvent("order-events", OrderProcessedEvent(
            eventType = "order.processed",
            orderId = savedOrder.id.toString(),
            userId = event.userId,
            totalAmount = savedOrder.totalAmount,
            status = savedOrder.status.name,
            createdAt = savedOrder.createdAt
        ), kafkaTemplate)
    }
    
    fun getOrdersByUser(userId: Long): List<Order> {
        return orderRepository.findByUserId(userId)
    }
}
