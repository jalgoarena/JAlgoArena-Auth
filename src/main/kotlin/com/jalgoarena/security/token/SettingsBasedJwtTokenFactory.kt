package com.jalgoarena.security.token

import com.jalgoarena.security.config.JwtSettings
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
open class SettingsBasedJwtTokenFactory(
        @Autowired private val settings: JwtSettings
): JwtTokenFactory {

    override fun generateToken(userDetails: UserDetails) = validate(userDetails) {
        val claims = Jwts.claims().setSubject(userDetails.username)
        claims.put("scopes", userDetails.authorities.map { s -> s.toString() })

        val currentTime = DateTime()

        Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.tokenIssuer)
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusMinutes(settings.tokenExpirationTime!!).toDate())
                .signWith(SignatureAlgorithm.HS512, settings.tokenSigningKey)
                .compact()
    }

    private fun validate(userDetails: UserDetails, block: () -> String): String {
        if (userDetails.username.isBlank())
            throw IllegalArgumentException("Cannot create JWT Token without username")

        if (userDetails.authorities.isEmpty())
            throw IllegalArgumentException("User doesn't have any privileges")

        return block()
    }
}

