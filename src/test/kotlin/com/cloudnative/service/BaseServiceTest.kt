package com.cloudnative.service

import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate

@ExtendWith(MockitoExtension::class)
open class BaseServiceTest<T : Any> {

    @Mock
    protected lateinit var kafkaTemplate: KafkaTemplate<String, T>
}
