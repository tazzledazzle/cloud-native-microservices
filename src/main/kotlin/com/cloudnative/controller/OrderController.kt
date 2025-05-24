package com.cloudnative.controller

import com.cloudnative.model.Order
import com.cloudnative.service.OrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/orders")
class OrderController(
    private val orderService: OrderService
) {
    
    @GetMapping("/user/{userId}")
    fun getOrdersByUser(@PathVariable userId: Long): ResponseEntity<List<Order>> {
        return ResponseEntity.ok(orderService.getOrdersByUser(userId))
    }
}
