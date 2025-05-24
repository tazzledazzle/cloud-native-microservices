package com.cloudnative.service

import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.model.User
import com.cloudnative.repository.UserRepository
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import jakarta.validation.Valid
import jakarta.validation.ConstraintViolationException
import org.springframework.validation.annotation.Validated
import java.time.LocalDateTime

class UserNotFoundException(id: Long) : RuntimeException("User not found with id: $id")

@Service
@Validated
class UserService(
    private val userRepository: UserRepository,
    private val kafkaTemplate: KafkaTemplate<String, UserCreatedEvent>,
    @Value("\${kafka.user-events-topic}") private val userEventsTopic: String,
    private val meterRegistry: MeterRegistry
) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    /**
     * Creates a user, emits a Kafka user.created event, and records a metric.
     * @throws ConstraintViolationException if input is invalid
     */
    fun createUser(@Valid user: User): User {
        val now = LocalDateTime.now()
        val userWithTimestamp = user.copy(createdAt = now)
        val savedUser = userRepository.save(userWithTimestamp)

        // Build and send event
        val event = UserCreatedEvent(
            eventType = "user.created",
            userId = savedUser.id.toString(),
            firstName = savedUser.name,
            lastName = "", // No last name in our User model
            email = savedUser.email,
            createdAt = savedUser.createdAt ?: now
        )
        kafkaTemplate.send(userEventsTopic, savedUser.id.toString(), event)
        logger.info("Published UserCreatedEvent for user id=${savedUser.id}")

        meterRegistry.counter("user.created.count",
            "userId", savedUser.id.toString(),
            "emailDomain", savedUser.email.substringAfter("@", "unknown")
        ).increment()

        return savedUser
    }

    /**
     * Finds a user by ID or throws a custom not-found exception.
     */
    fun getUser(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }
    }
}

// Using com.cloudnative.common.events.UserCreatedEvent
// Remove local UserCreatedEvent class since we're using the common events version