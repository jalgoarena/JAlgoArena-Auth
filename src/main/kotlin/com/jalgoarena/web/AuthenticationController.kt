package com.jalgoarena.web

import com.jalgoarena.data.UsersRepository
import com.jalgoarena.security.auth.JwtAuthenticationResponse
import com.jalgoarena.security.auth.LoginRequest
import com.jalgoarena.security.token.JwtTokenFactory
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
import javax.inject.Inject

@RestController
class AuthenticationController {

    @Inject
    private lateinit var authenticationManager: AuthenticationManager

    @Inject
    private lateinit var jwtTokenFactory: JwtTokenFactory

    @Inject
    private lateinit var userDetailsService: UserDetailsService

    @Inject
    private lateinit var repository: UsersRepository

    @PostMapping("/login", produces = [(MediaType.APPLICATION_JSON_VALUE)])
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<JwtAuthenticationResponse>? {

        val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        loginRequest.username,
                        loginRequest.password
                )
        )
        SecurityContextHolder.getContext().authentication = authentication

        val userDetails = userDetailsService.loadUserByUsername(loginRequest.username)
        val token = jwtTokenFactory.generateToken(userDetails)

        val user = repository.findByUsername(loginRequest.username).apply {
            password = ""
        }

        return ok(JwtAuthenticationResponse(token, user))
    }
}
