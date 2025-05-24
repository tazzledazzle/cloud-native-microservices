package com.cloudnative.service

import org.springframework.kafka.core.KafkaTemplate

abstract class BaseService<T : Any>(
    protected open val kafkaTemplate: KafkaTemplate<String, T>? = null
) {
    
    protected fun publishEvent(topic: String, event: T) {
        kafkaTemplate?.send(topic, event)
    }
}
