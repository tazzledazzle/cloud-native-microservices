package com.cloudnative.service

import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.model.User
import com.cloudnative.repository.UserRepository
import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import jakarta.validation.Valid
import jakarta.validation.ConstraintViolationException
import java.time.LocalDateTime as JLocalDateTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.springframework.validation.annotation.Validated
import org.springframework.transaction.annotation.Transactional

class UserNotFoundException(id: Long) : RuntimeException("User not found with id: $id")

@Validated
@Service
@Transactional
class UserService(
    private val userRepository: UserRepository,
    private val meterRegistry: MeterRegistry,
    override val kafkaTemplate: KafkaTemplate<String, UserCreatedEvent>,
    @Value("\${user.events.topic}") private val userEventsTopic: String
) : BaseService<UserCreatedEvent>(kafkaTemplate) {
    private val logger = LoggerFactory.getLogger(UserService::class.java)

    fun createUser(@Valid user: User): User {
        val now = JLocalDateTime.now().toKotlinLocalDateTime()
        val userWithTimestamp = user.copy(createdAt = now)
        val savedUser = userRepository.save(userWithTimestamp)

        val event = UserCreatedEvent(
            eventType = "user.created",
            userId = savedUser.id.toString(),
            firstName = savedUser.firstName,
            lastName = savedUser.lastName,
            email = savedUser.email,
            createdAt = savedUser.createdAt ?: JLocalDateTime.now().toKotlinLocalDateTime()
        )
        publishEvent(userEventsTopic, savedUser.id.toString(), event)

        meterRegistry.counter("user.created.count",
            "userId", savedUser.id.toString(),
            "emailDomain", savedUser.email.substringAfter("@", "unknown")
        ).increment()

        return savedUser
    }

    fun getUser(id: Long): User {
        return userRepository.findById(id)
            .orElseThrow { UserNotFoundException(id) }
    }
}
