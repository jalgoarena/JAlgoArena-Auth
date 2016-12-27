package com.jalgoarena.security.model.token

import com.jalgoarena.security.exceptions.JwtExpiredTokenException
import io.jsonwebtoken.*
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException

class RawAccessJwtToken(override val token: String) : JwtToken {

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
        throw JwtExpiredTokenException(this, "JWT Token expired", expiredEx)
    }
}
