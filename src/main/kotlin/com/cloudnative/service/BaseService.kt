package com.cloudnative.service

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate

abstract class BaseService<T : Any>(
    protected open val kafkaTemplate: KafkaTemplate<String, T>
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    protected fun publishEvent(topic: String, event: T) {
        val future = kafkaTemplate.send(topic, event) ?: return
        future.whenComplete { _, ex ->
            if (ex != null) logger.error("Failed to publish {} to {}: {}", event::class.simpleName, topic, ex.message, ex)
            else logger.debug("Published {} to {}", event::class.simpleName, topic)
        }
    }

    protected fun publishEvent(topic: String, key: String, event: T) {
        val future = kafkaTemplate.send(topic, key, event) ?: return
        future.whenComplete { _, ex ->
            if (ex != null) logger.error("Failed to publish {} to {}: {}", event::class.simpleName, topic, ex.message, ex)
            else logger.debug("Published {} key={} to {}", event::class.simpleName, key, topic)
        }
    }
}
