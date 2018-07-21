package com.jalgoarena.security.auth

import com.jalgoarena.data.User

data class JwtAuthenticationResponse(val token: String, val user: User)
