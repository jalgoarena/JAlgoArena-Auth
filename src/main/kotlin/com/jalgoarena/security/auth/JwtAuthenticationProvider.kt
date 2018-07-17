package com.jalgoarena.security.auth

import com.jalgoarena.security.config.JwtSettings
import com.jalgoarena.security.token.JwtAuthenticationToken
import com.jalgoarena.security.token.JwtToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.util.*

@Component
open class JwtAuthenticationProvider(
        @Autowired private val jwtSettings: JwtSettings
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val rawAccessToken = authentication.credentials as JwtToken

        val jwsClaims = rawAccessToken.parseClaims(jwtSettings.tokenSigningKey!!)
        val subject = jwsClaims.body.subject
        val scopes = jwsClaims.body.get("scopes", ArrayList::class.java)
        val authorities = scopes.map {authority ->
            SimpleGrantedAuthority(authority as String)
        }

        val user = User(subject, "", authorities)
        return JwtAuthenticationToken(user, user.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return JwtAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
