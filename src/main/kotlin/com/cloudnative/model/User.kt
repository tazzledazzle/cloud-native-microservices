package com.cloudnative.model

import javax.persistence.*
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

@Entity
data class User(
    @Id @GeneratedValue val id: Long = 0,
    @field:NotBlank val name: String,
    @field:Email val email: String,
    val createdAt: LocalDateTime? = null
)