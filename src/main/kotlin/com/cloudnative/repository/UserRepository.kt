package com.cloudnative.repository

import com.cloudnative.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    override fun findById(id: Long): Optional<User>
}
