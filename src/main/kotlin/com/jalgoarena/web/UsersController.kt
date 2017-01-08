package com.jalgoarena.web

import com.jalgoarena.data.UsersRepository
import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User
import com.jalgoarena.security.auth.JwtAuthenticationToken
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject

@RestController
class UsersController(@Inject private val repository: UsersRepository) {

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
    fun user(token: JwtAuthenticationToken) = repository.findByUsername(token.principal!!.username).apply {
        password = ""
    }

    @PostMapping("/api/signup", produces = arrayOf("application/json"))
    fun signup(@RequestBody user: User) =
            ResponseEntity(repository.addUser(user), HttpStatus.CREATED)
}
