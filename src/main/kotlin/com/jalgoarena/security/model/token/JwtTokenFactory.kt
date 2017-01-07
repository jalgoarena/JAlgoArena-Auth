package com.jalgoarena.security.model.token

import com.jalgoarena.security.config.JwtSettings
import com.jalgoarena.security.model.UserContext
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.commons.lang.StringUtils
import org.joda.time.DateTime
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
open class JwtTokenFactory(@Inject private val settings: JwtSettings) {

    fun createAccessJwtToken(userContext: UserContext): AccessJwtToken {
        if (StringUtils.isBlank(userContext.username))
            throw IllegalArgumentException("Cannot create JWT Token without username")

        if (userContext.authorities.isEmpty())
            throw IllegalArgumentException("User doesn't have any privileges")

        val claims = Jwts.claims().setSubject(userContext.username)
        claims.put("scopes", userContext.authorities.map({ s -> s.toString() }))

        val currentTime = DateTime()

        val token = Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.tokenIssuer)
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusMinutes(settings.tokenExpirationTime!!).toDate())
                .signWith(SignatureAlgorithm.HS512, settings.tokenSigningKey)
                .compact()

        return AccessJwtToken(token, claims)
    }
}
