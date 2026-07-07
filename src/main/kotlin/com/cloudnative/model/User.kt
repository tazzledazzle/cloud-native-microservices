package com.cloudnative.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Email
import java.time.LocalDateTime


@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    @field:NotBlank val firstName: String,
    @field:NotBlank val lastName: String,
    @field:Email val email: String,
    val createdAt: LocalDateTime? = null
)