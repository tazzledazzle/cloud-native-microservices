package com.cloudnative.model

import jakarta.persistence.*
import kotlinx.datetime.LocalDateTime
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Email
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType


@Entity
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long = 0,
    @field:NotBlank val name: String,
    @field:Email val email: String,
    val createdAt: LocalDateTime? = null
)