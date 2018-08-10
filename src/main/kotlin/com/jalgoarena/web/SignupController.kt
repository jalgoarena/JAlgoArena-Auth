package com.jalgoarena.web

import com.jalgoarena.data.EmailIsAlreadyUsedException
import com.jalgoarena.data.UserRepository
import com.jalgoarena.data.UsernameIsAlreadyUsedException
import com.jalgoarena.domain.User
import org.intellij.lang.annotations.Language
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.CacheManager
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
open class SignupController(
        @Autowired private val cacheManager: CacheManager,
        @Autowired private val repository: UserRepository
) {
    @PostMapping("/signup", produces = ["application/json"])
    fun signup(@RequestBody user: User) =
            checkIfUsernameOrEmailIsAlreadyUsed(user).let { newUser ->
                ResponseEntity(repository.save(
                        newUser.apply { password = BCryptPasswordEncoder().encode(newUser.password) }
                ).also {
                    clearCache()
                }.copy(password = ""), HttpStatus.CREATED)
            }

    @ExceptionHandler
    fun usernameIsAlreadyUsed(ex: UsernameIsAlreadyUsedException) =
            error(ex.message!!)

    @ExceptionHandler
    fun emailIsAlreadyUsed(ex: EmailIsAlreadyUsedException) =
            error(ex.message!!)

    @Language("JSON")
    private fun error(message: String) =
            ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{ \"error\": \"Registration Error\", \"message\": \"$message\" }")

    private fun clearCache() {
        cacheManager.cacheNames.parallelStream().forEach {
            cacheManager.getCache(it)!!.clear()
        }
    }

    private fun checkIfUsernameOrEmailIsAlreadyUsed(user: User) =
            when {
                repository.findByUsername(user.username).isPresent -> throw UsernameIsAlreadyUsedException()
                repository.findByEmail(user.email).isPresent -> throw EmailIsAlreadyUsedException()
                else -> user
            }
}