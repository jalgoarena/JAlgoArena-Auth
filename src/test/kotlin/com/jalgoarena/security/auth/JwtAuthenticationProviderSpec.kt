package com.jalgoarena.security.auth

import com.jalgoarena.security.config.JwtSettings
import com.jalgoarena.security.token.JwtAuthenticationToken
import com.jalgoarena.security.token.JwtToken
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.impl.DefaultJws
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class JwtAuthenticationProviderSpec {

    private val jwtSettings = mock(JwtSettings::class.java)
    private val jwtToken = mock(JwtToken::class.java)
    private val authentication = mock(Authentication::class.java)
    private val jwtAuthenticationProvider = JwtAuthenticationProvider(jwtSettings)

    private val DUMMY_TOKEN_SIGNING_KEY = "&*(ASD*(&S*DSDSDAS"

    @Test
    fun supports_jwt_authentication_token() {
        val isSupported = jwtAuthenticationProvider.supports(JwtAuthenticationToken::class.java)
        assertThat(isSupported).isTrue()
    }

    @Test
    fun parses_token_and_returns_authenticated_token() {
        given(authentication.credentials).willReturn(jwtToken)
        given(jwtSettings.tokenSigningKey).willReturn(DUMMY_TOKEN_SIGNING_KEY)
        given(jwtToken.parseClaims(DUMMY_TOKEN_SIGNING_KEY))
                .willReturn(DefaultJws<Claims>(null, claims(), null))

        val authenticatedToken = jwtAuthenticationProvider.authenticate(authentication)

        assertThat(authenticatedToken.isAuthenticated).isTrue()
        assertThat(authenticatedToken.credentials).isNull()
        assertThat(authenticatedToken.principal).isEqualTo(USER)
    }

    private fun claims(): Claims {
        val claims = Jwts.claims().setSubject(USER.username)
        claims.put("scopes", USER.authorities.map({ s -> s.toString() }))
        return claims
    }

    private val USER = User("julia", "", listOf(SimpleGrantedAuthority("ROLE_USER")))
}
