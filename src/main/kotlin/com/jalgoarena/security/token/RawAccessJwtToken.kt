package com.jalgoarena.security.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException

data class RawAccessJwtToken(override val token: String) : JwtToken {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun parseClaims(signingKey: String): Jws<Claims> = try {
        Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token)
    } catch(e: Throwable) {
        when (e) {
            is ExpiredJwtException -> throw JwtExpiredTokenException("JWT Token expired", e)
            else -> {
                logger.error("Invalid JWT Token", e)
                throw BadCredentialsException("Invalid JWT token: ", e)
            }
        }
    }
}
