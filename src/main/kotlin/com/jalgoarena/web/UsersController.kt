package com.jalgoarena.web

import com.jalgoarena.data.EmailIsAlreadyUsedException
import com.jalgoarena.data.User
import com.jalgoarena.data.UsernameIsAlreadyUsedException
import com.jalgoarena.data.UserRepository
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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
            repository.findByUsername(principal.name).first().apply { password = "" }

    @PostMapping("/signup", produces = ["application/json"])
    fun signup(@RequestBody user: User) =
            ResponseEntity(
                    repository.save(user)
                            .apply { password = "" },
                    HttpStatus.CREATED
            )

    @Language("JSON")
    private fun error(message: String) = ResponseEntity.status(HttpStatus.CONFLICT)
            .body("{ \"error\": \"Registration Error\", \"message\": \"$message\" }")

    @ExceptionHandler
    fun usernameIsAlreadyUsed(ex: UsernameIsAlreadyUsedException)
            = error(ex.message!!)

    @ExceptionHandler
    fun emailIsAlreadyUsed(ex: EmailIsAlreadyUsedException)
            = error(ex.message!!)
}
