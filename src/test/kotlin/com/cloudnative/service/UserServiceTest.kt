package com.cloudnative.service

import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.model.User
import com.cloudnative.repository.UserRepository
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate
import java.util.Optional

@ExtendWith(MockitoExtension::class)
class UserServiceTest : BaseServiceTest<UserCreatedEvent>() {

    @Mock
    lateinit var userRepository: UserRepository

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        @Suppress("UNCHECKED_CAST")
        userService = UserService(
            userRepository = userRepository,
            meterRegistry = SimpleMeterRegistry(),
            kafkaTemplate = kafkaTemplate as KafkaTemplate<String, UserCreatedEvent>,
            userEventsTopic = "user-events"
        )
    }

    @Test
    fun `should create user and publish event`() {
        val user = User(firstName = "Test", lastName = "User", email = "test@example.com")
        val savedUser = User(id = 1L, firstName = "Test", lastName = "User", email = "test@example.com")

        Mockito.`when`(userRepository.save(any(User::class.java))).thenReturn(savedUser)

        val result = userService.createUser(user)

        Mockito.verify(userRepository).save(any(User::class.java))
        assertEquals(savedUser, result)
    }

    @Test
    fun `should get user by id`() {
        val user = User(id = 1L, firstName = "Test", lastName = "User", email = "test@example.com")

        Mockito.`when`(userRepository.findById(1L)).thenReturn(Optional.of(user))

        val result = userService.getUser(1L)

        Mockito.verify(userRepository).findById(1L)
        assertEquals(user, result)
    }
}
