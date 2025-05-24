package com.cloudnative.controller

import com.cloudnative.model.User
import com.cloudnative.service.UserService
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    
    @PostMapping
    fun createUser(@RequestBody user: User): User {
        return userService.createUser(user)
    }
    
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): User {
        return userService.getUser(id)
    }
}
