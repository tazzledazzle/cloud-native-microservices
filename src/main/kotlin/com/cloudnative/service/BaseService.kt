package com.cloudnative.service

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

abstract class BaseService {
    protected fun <T> publishEvent(topic: String, event: T, kafkaTemplate: KafkaTemplate<String, T>) {
        kafkaTemplate.send(topic, event)
    }
}
