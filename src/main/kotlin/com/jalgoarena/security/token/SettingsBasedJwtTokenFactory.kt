package com.jalgoarena.security.token

import com.jalgoarena.security.config.JwtSettings
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.commons.lang.StringUtils
import org.joda.time.DateTime
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
open class SettingsBasedJwtTokenFactory(@Inject private val settings: JwtSettings): JwtTokenFactory {

    override fun generateToken(userDetails: UserDetails): String {
        if (StringUtils.isBlank(userDetails.username))
            throw IllegalArgumentException("Cannot create JWT Token without username")

        if (userDetails.authorities.isEmpty())
            throw IllegalArgumentException("User doesn't have any privileges")

        val claims = Jwts.claims().setSubject(userDetails.username)
        claims.put("scopes", userDetails.authorities.map({ s -> s.toString() }))

        val currentTime = DateTime()

        return Jwts.builder()
                .setClaims(claims)
                .setIssuer(settings.tokenIssuer)
                .setIssuedAt(currentTime.toDate())
                .setExpiration(currentTime.plusMinutes(settings.tokenExpirationTime!!).toDate())
                .signWith(SignatureAlgorithm.HS512, settings.tokenSigningKey)
                .compact()
    }
}

