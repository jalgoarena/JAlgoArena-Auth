package com.jalgoarena.security.token

import org.springframework.security.core.userdetails.UserDetails

interface JwtTokenFactory {
    fun generateToken(userDetails: UserDetails): String
}
