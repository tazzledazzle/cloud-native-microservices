package com.cloudnative.service

import com.cloudnative.common.events.UserCreatedEvent
import com.cloudnative.common.events.OrderProcessedEvent
import com.cloudnative.model.User
import com.cloudnative.repository.UserRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import kotlinx.datetime.LocalDateTime

@ExtendWith(MockitoExtension::class)
class UserServiceTest : BaseServiceTest<BaseService<UserCreatedEvent>>() {

    @Mock
    lateinit var userRepository: UserRepository

    @InjectMocks
    lateinit var userService: UserService

    @Test
    fun `should create user and publish event`() {
        val user = User(
            email = "test@example.com",
            name = "Test User"
        )

        val savedUser = User(
            id = 1L,
            email = "test@example.com",
            name = "Test User"
        )

        Mockito.`when`(userRepository.save(user)).thenReturn(savedUser)

        val result = userService.createUser(user)

        Mockito.verify(userRepository).save(user)
        Mockito.verify(kafkaTemplate).send(
            "user-events",
            UserCreatedEvent(
                eventType = "user.created",
                userId = "1",
                name = "Test User",
                email = "test@example.com",
                createdAt = savedUser.createdAt!!
            )
        )

        assert(result == savedUser)
    }

    @Test
    fun `should get user by id`() {
        val user = User(
            id = 1L,
            name = "Test User",
            email = "test@example.com"
        )

        Mockito.`when`(userRepository.findById(1L)).thenReturn(java.util.Optional.of(user))

        val result = userService.getUser(1L)

        Mockito.verify(userRepository).findById(1L)
        assert(result == user)
    }
}
