package com.jalgoarena.security.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws

interface JwtToken {
    val token: String
    fun parseClaims(signingKey: String): Jws<Claims>
}
