package com.cloudnative.service

import com.cloudnative.model.User
import com.cloudnative.repository.UserRepository
import com.cloudnative.common.events.UserCreatedEvent
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var kafkaTemplate: KafkaTemplate<String, UserCreatedEvent>

    @InjectMocks
    lateinit var userService: UserService

    @Test
    fun `should create user and publish event`() {
        val user = User(
            email = "test@example.com",
            firstName = "Test",
            lastName = "User"
        )

        val savedUser = User(
            id = 1L,
            email = "test@example.com",
            firstName = "Test",
            lastName = "User"
        )

        Mockito.`when`(userRepository.save(user)).thenReturn(savedUser)

        val result = userService.createUser(user)

        Mockito.verify(userRepository).save(user)
        Mockito.verify(kafkaTemplate).send(
            "user-events",
            UserCreatedEvent(
                eventType = "user.created",
                userId = "1",
                email = "test@example.com",
                createdAt = savedUser.createdAt
            )
        )

        assert(result.id == 1L)
    }

    @Test
    fun `should get user by id`() {
        val user = User(
            id = 1L,
            email = "test@example.com",
            firstName = "Test",
            lastName = "User"
        )

        Mockito.`when`(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user))

        val result = userService.getUser(1L)

        Mockito.verify(userRepository).findById(1L)
        assert(result.id == 1L)
    }
}
