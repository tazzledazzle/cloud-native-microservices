package com.cloudnative

import com.cloudnative.model.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(properties = [
    "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password=",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.cloud.config.enabled=false",
    "spring.cloud.compatibility-verifier.enabled=false",
    "spring.kafka.bootstrap-servers=localhost:9092",
    "user.events.topic=user-events"
])
class ApplicationContextTest {

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Test
    fun `application context loads without config server or Kafka`() {
        assert(applicationContext != null)
    }

    @Test
    fun `User entity createdAt field is java time LocalDateTime`() {
        val now = LocalDateTime.now()
        val user = User(firstName = "Test", lastName = "User", email = "test@example.com", createdAt = now)
        assert(user.createdAt is LocalDateTime)
        assert(user.createdAt != null)
    }
}
