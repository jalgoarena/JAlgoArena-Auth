package com.jalgoarena.security.exceptions

import com.jalgoarena.security.model.token.JwtToken
import org.springframework.security.core.AuthenticationException

class JwtExpiredTokenException(
        val token: JwtToken, msg: String, t: Throwable
) : AuthenticationException(msg, t) {

    fun token(): String? = token.token
}
