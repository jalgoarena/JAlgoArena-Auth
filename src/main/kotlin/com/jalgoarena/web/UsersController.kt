package com.jalgoarena.web

import com.jalgoarena.data.User
import com.jalgoarena.data.UserRepository
import com.jalgoarena.domain.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
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
    fun publicUsers() =
            repository.findAll().map { it.copy(password = "") }

    @PutMapping("/api/users", produces = ["application/json"])
    fun updateUser(@RequestBody user: User, principal: Principal): ResponseEntity<User?> {
        val admin = repository.findByUsername(principal.name)

        return if (admin.isPresent && admin.get().role == Role.ADMIN.toString()) {
            ResponseEntity(
                    repository.save(user.apply {password = BCryptPasswordEncoder().encode(user.password)})
                            .copy(password = ""),
                    HttpStatus.ACCEPTED
            )
        } else {
            ResponseEntity(
                    null as User?,
                    HttpStatus.FORBIDDEN
            )
        }
    }

    @GetMapping("/api/user", produces = ["application/json"])
    fun checkSession(principal: Principal) =
            repository.findByUsername(principal.name).get().copy(password = "")
}
