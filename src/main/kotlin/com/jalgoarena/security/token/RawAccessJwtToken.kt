package com.jalgoarena.security.token

import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException

data class RawAccessJwtToken(val token: String) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    fun parseClaims(signingKey: String): Jws<Claims> = try {
        Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token)
    } catch (ex: UnsupportedJwtException) {
        logger.error("Invalid JWT Token", ex)
        throw BadCredentialsException("Invalid JWT token: ", ex)
    } catch (ex: MalformedJwtException) {
        logger.error("Invalid JWT Token", ex)
        throw BadCredentialsException("Invalid JWT token: ", ex)
    } catch (ex: IllegalArgumentException) {
        logger.error("Invalid JWT Token", ex)
        throw BadCredentialsException("Invalid JWT token: ", ex)
    } catch (ex: SignatureException) {
        logger.error("Invalid JWT Token", ex)
        throw BadCredentialsException("Invalid JWT token: ", ex)
    } catch (expiredEx: ExpiredJwtException) {
        logger.info("JWT Token is expired", expiredEx)
        throw JwtExpiredTokenException("JWT Token expired", expiredEx)
    }
}
