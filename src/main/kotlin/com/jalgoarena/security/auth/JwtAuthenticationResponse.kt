package com.jalgoarena.security.auth

import com.jalgoarena.domain.User

data class JwtAuthenticationResponse(val token: String, val user: User)
