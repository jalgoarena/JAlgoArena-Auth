package com.jalgoarena.web

import com.jalgoarena.data.UserDetailsRepository
import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User
import com.jalgoarena.security.auth.JwtAuthenticationToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class UsersController {

    @Autowired
    lateinit var repository: UserDetailsRepository

    @GetMapping("/users", produces = arrayOf("application/json"))
    fun publicUsers(): List<User> {
        return repository.findAll().map { User(
                it.username,
                "",
                "",
                it.region,
                it.team,
                Role.USER,
                it.id
        ) }
    }

    @GetMapping("/api/users", produces = arrayOf("application/json"))
    fun users(): List<User> {
        return repository.findAll().map { User(
                it.username,
                "",
                it.email,
                it.region,
                it.team,
                it.role,
                it.id
        ) }
    }

    @GetMapping("/api/user", produces = arrayOf("application/json"))
    fun user(token: JwtAuthenticationToken): User? {
        val user = repository.findByUsername(token.principal!!.username)

        if (user != null) {
            user.password = ""
        }

        return user
    }

    @PostMapping("/api/auth/signup", produces = arrayOf("application/json"))
    fun signup(@RequestBody userDetails: User) = repository.addUser(userDetails)
}
