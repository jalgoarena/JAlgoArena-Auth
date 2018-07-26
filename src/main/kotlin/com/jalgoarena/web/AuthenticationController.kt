package com.jalgoarena.web

import com.jalgoarena.data.UserRepository
import com.jalgoarena.security.auth.JwtAuthenticationResponse
import com.jalgoarena.security.auth.LoginRequest
import com.jalgoarena.security.token.JwtTokenFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.ResponseEntity.ok
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authenticationManager: AuthenticationManager

    @Autowired
    private lateinit var jwtTokenFactory: JwtTokenFactory

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var repository: UserRepository

    @PostMapping("/login", produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<JwtAuthenticationResponse>? {

        try {
            val authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(
                            loginRequest.username,
                            loginRequest.password
                    )
            )
            SecurityContextHolder.getContext().authentication = authentication

            val userDetails = userDetailsService.loadUserByUsername(loginRequest.username)
            val token = jwtTokenFactory.generateToken(userDetails)

            val user = repository.findByUsername(loginRequest.username).get().apply {
                password = ""
            }

            return ok(JwtAuthenticationResponse(token, user))
        } catch (e: Exception) {
            log.error("[err] POST /login: {}", e.message, e)
            throw e
        }
    }
}
