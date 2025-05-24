package com.cloudnative.service

import org.springframework.kafka.core.KafkaTemplate
import java.time.LocalDateTime

abstract class BaseService(
    protected val kafkaTemplate: KafkaTemplate<String, Any>? = null
) {
    
    protected fun publishEvent(topic: String, event: Any, kafkaTemplate: KafkaTemplate<String, Any> = this.kafkaTemplate!!) {
        kafkaTemplate.send(topic, event)
    }
}
