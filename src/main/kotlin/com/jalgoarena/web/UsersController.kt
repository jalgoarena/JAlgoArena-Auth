package com.jalgoarena.web

import com.jalgoarena.data.EmailIsAlreadyUsedException
import com.jalgoarena.data.UsernameIsAlreadyUsedException
import com.jalgoarena.data.UsersRepository
import com.jalgoarena.domain.User
import org.intellij.lang.annotations.Language
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import javax.inject.Inject

@RestController
class UsersController(@Inject private val repository: UsersRepository) {

    @GetMapping("/users", produces = arrayOf("application/json"))
    fun publicUsers() = repository.findAll().map {
        it.copy(password = "", email = "")
    }

    @PutMapping("/api/users", produces = arrayOf("application/json"))
    fun updateUser(@RequestBody user: User) =
            repository.update(user).copy(password = "")

    @GetMapping("/api/users", produces = arrayOf("application/json"))
    fun users() = repository.findAll().map { it.apply { password = "" } }

    @GetMapping("/api/user", produces = arrayOf("application/json"))
    fun user(principal: Principal) =
            repository.findByUsername(principal.name).apply { password = "" }

    @PostMapping("/signup", produces = arrayOf("application/json"))
    fun signup(@RequestBody user: User): ResponseEntity<*> = handleExceptions {
        ResponseEntity(repository.add(user).apply { password = "" }, HttpStatus.CREATED)
    }

    private fun handleExceptions(body: () -> ResponseEntity<*>): ResponseEntity<*> = try {
        body()
    } catch(e: Throwable) {
        @Language("JSON")
        fun error(message: String) = ResponseEntity.status(HttpStatus.CONFLICT)
                .body("{ \"error\": \"Registration Error\", \"message\": \"$message\" }")

        when (e) {
            is EmailIsAlreadyUsedException -> error("Email is already used")
            is UsernameIsAlreadyUsedException -> error("Username is already used")
            else -> throw e
        }
    }
}
