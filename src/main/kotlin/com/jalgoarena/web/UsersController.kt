package com.jalgoarena.web

import com.jalgoarena.domain.User
import com.jalgoarena.data.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
open class UsersController(
        @Autowired private val usersRepository: UserRepository
) {

    @GetMapping("/users", produces = ["application/json"])
    open fun publicUsers() =
            usersRepository.findAll().map { it.copy(password = "") }

    @PutMapping("/api/users", produces = ["application/json"])
    open fun updateUser(@RequestBody user: User) =
            usersRepository.save(user.apply {password = BCryptPasswordEncoder().encode(user.password)})
                    .copy(password = "")

    @GetMapping("/api/user", produces = ["application/json"])
    open fun checkSession(principal: Principal) =
            usersRepository.findByUsername(principal.name).get().copy(password = "")
}
