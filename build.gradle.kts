plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "2.1.20"
    kotlin("plugin.spring") version "2.1.20"
}

group = "com.cloudnative"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_23

repositories {
    mavenCentral()
}

dependencies {

    // Kotlin deps
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
    
    // Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")
    
    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-spring-boot2")
    
    // Jakarta EE
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:junit-jupiter")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(23)
}

// Configure Spring Cloud version
dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2022.0.4")
    }
}

tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    manifest {
        attributes("Main-Class" to "com.cloudnative.UserApplication")
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }
    with(tasks.jar.get())
}
