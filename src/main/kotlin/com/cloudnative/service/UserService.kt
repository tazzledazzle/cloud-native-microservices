package com.cloudnative.service

import com.cloudnative.model.User
import com.cloudnative.repository.UserRepository
import com.cloudnative.common.events.UserCreatedEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserService(
    private val userRepository: UserRepository,
    private val kafkaTemplate: KafkaTemplate<String, UserCreatedEvent>
) : BaseService() {
    
    fun createUser(user: User): User {
        val savedUser = userRepository.save(user)
        
        // Publish user.created event
        val event = UserCreatedEvent(
            eventType = "user.created",
            userId = savedUser.id.toString(),
            email = savedUser.email,
            createdAt = savedUser.createdAt
        )
        publishEvent("user-events", event, kafkaTemplate)
        
        return savedUser
    }

    fun getUser(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { RuntimeException("User not found with id: $id") }
    }
}
