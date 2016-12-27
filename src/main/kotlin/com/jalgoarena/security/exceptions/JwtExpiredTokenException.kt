package com.jalgoarena.security.exceptions

import com.jalgoarena.security.model.token.JwtToken
import org.springframework.security.core.AuthenticationException

class JwtExpiredTokenException : AuthenticationException {

    private var token: JwtToken? = null

    constructor(msg: String) : super(msg) {
    }

    constructor(token: JwtToken, msg: String, t: Throwable) : super(msg, t) {
        this.token = token
    }

    fun token(): String = token!!.token
}
