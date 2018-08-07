package com.jalgoarena.web

import com.jalgoarena.data.EmailIsAlreadyUsedException
import com.jalgoarena.data.UserRepository
import com.jalgoarena.data.UsernameIsAlreadyUsedException
import com.jalgoarena.domain.User
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class SignupController(
        @Autowired private val repository: UserRepository
) {
    @PostMapping("/signup", produces = ["application/json"])
    fun signup(@RequestBody user: User): ResponseEntity<User> {
        checkIfUsernameOrEmailIsAlreadyUsed(user)

        return ResponseEntity(
                repository.save(user.apply {password = BCryptPasswordEncoder().encode(user.password)})
                        .copy(password = ""),
                HttpStatus.CREATED
        )
    }

    @ExceptionHandler
    fun usernameIsAlreadyUsed(ex: UsernameIsAlreadyUsedException)
            = error(ex.message!!)

    @ExceptionHandler
    fun emailIsAlreadyUsed(ex: EmailIsAlreadyUsedException)
            = error(ex.message!!)

    @Language("JSON")
    private fun error(message: String) = ResponseEntity.status(HttpStatus.CONFLICT)
            .body("{ \"error\": \"Registration Error\", \"message\": \"$message\" }")

    private fun checkIfUsernameOrEmailIsAlreadyUsed(user: User) {

        if (repository.findByUsername(user.username).isPresent) {
            throw UsernameIsAlreadyUsedException()
        }

        if (repository.findByEmail(user.email).isPresent) {
            throw EmailIsAlreadyUsedException()
        }
    }
}