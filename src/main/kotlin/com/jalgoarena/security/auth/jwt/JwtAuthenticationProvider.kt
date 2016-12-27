package com.jalgoarena.security.auth.jwt

import com.jalgoarena.security.auth.JwtAuthenticationToken
import com.jalgoarena.security.config.JwtSettings
import com.jalgoarena.security.model.UserContext
import com.jalgoarena.security.model.token.RawAccessJwtToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.util.*
import javax.inject.Inject

@Component
open class JwtAuthenticationProvider(
        @Inject private val jwtSettings: JwtSettings
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val rawAccessToken = authentication.credentials as RawAccessJwtToken

        val jwsClaims = rawAccessToken.parseClaims(jwtSettings.tokenSigningKey!!)
        val subject = jwsClaims.body.subject
        val scopes = jwsClaims.body.get("scopes", ArrayList::class.java)
        val authorities = scopes.map {authority ->
            SimpleGrantedAuthority(authority as String)
        }

        val context = UserContext.create(subject, authorities)

        return JwtAuthenticationToken(context, context.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
