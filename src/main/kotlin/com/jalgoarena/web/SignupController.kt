package com.jalgoarena.web

import com.jalgoarena.data.EmailIsAlreadyUsedException
import com.jalgoarena.data.User
import com.jalgoarena.data.UserRepository
import com.jalgoarena.data.UsernameIsAlreadyUsedException
import com.jalgoarena.domain.Role
import org.intellij.lang.annotations.Language
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*
import javax.annotation.PostConstruct

@RestController
open class SignupController(
        @Autowired private val repository: UserRepository
) {
    @PostMapping("/signup", produces = ["application/json"])
    fun signup(@RequestBody user: User): ResponseEntity<User> {
        checkIfUsernameOrEmailIsAlreadyUsed(user)

        return ResponseEntity(
                repository.save(user)
                        .apply { password = "" },
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

    @PostConstruct
    private fun createAdminUser() {
        val admin = repository.findByUsername(ADMIN_USERNAME)

        if (admin.isPresent) {
            LOG.info("Admin already exists.")
            return
        }

        val randomPassword = UUID.randomUUID().toString()

        repository.save(User(
                username = ADMIN_USERNAME,
                password = randomPassword,
                email = "admin@mail.com",
                region = "Admin",
                team = "Admin",
                role = Role.ADMIN.toString()
        ))

        LOG.info("Admin user created [username: $ADMIN_USERNAME, password: $randomPassword]")
    }

    companion object {
        private const val ADMIN_USERNAME = "admin"
        private val LOG = LoggerFactory.getLogger(SignupController::class.java)
    }
}