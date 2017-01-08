package com.jalgoarena.security.token

import org.springframework.security.core.AuthenticationException

class JwtExpiredTokenException(msg: String, t: Throwable) : AuthenticationException(msg, t)
