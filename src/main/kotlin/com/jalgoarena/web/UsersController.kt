package com.jalgoarena.web

import com.jalgoarena.data.User
import com.jalgoarena.data.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class UsersController(
        @Autowired private val repository: UserRepository
) {

    @GetMapping("/users", produces = ["application/json"])
    fun publicUsers() = repository.findAll().map {
        it.copy(password = "", email = "")
    }

    @PutMapping("/api/users", produces = ["application/json"])
    fun updateUser(@RequestBody user: User) =
            repository.save(user).copy(password = "")

    @GetMapping("/api/users", produces = ["application/json"])
    fun users() = repository.findAll().map { it.apply { password = "" } }

    @GetMapping("/api/user", produces = ["application/json"])
    fun user(principal: Principal) =
            repository.findByUsername(principal.name).get().apply { password = "" }
}
