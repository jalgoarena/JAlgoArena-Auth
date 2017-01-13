package com.jalgoarena.security.token

import com.jalgoarena.security.config.JwtSettings
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User

class RawAccessJwtTokenSpec {

    private val jwtSettings = mock(JwtSettings::class.java)
    private val DUMMY_TOKEN_SIGNING_KEY = "&*(ASD*(&S*DSDSDAS"
    private var token: String? = null

    @Before
    fun setUp() {
        given(jwtSettings.tokenSigningKey).willReturn(DUMMY_TOKEN_SIGNING_KEY)
        given(jwtSettings.tokenExpirationTime).willReturn(20000)

        val tokenFactory = SettingsBasedJwtTokenFactory(jwtSettings)
        token = tokenFactory.generateToken(USER)
    }

    @Test
    fun parses_authentication_token() {
        val claims = RawAccessJwtToken(token!!).parseClaims(DUMMY_TOKEN_SIGNING_KEY)
        assertThat(claims.body.subject).isEqualTo(USER.username)
    }

    @Test(expected = JwtExpiredTokenException::class)
    fun throws_expired_jwt_token_exception_when_token_expires() {
        given(jwtSettings.tokenExpirationTime).willReturn(0)
        RawAccessJwtToken(token!!).parseClaims(DUMMY_TOKEN_SIGNING_KEY)
    }

    @Test(expected = BadCredentialsException::class)
    fun throws_bad_credentials_exception_when_token_is_corrupted() {
        RawAccessJwtToken("corrupted_token").parseClaims(DUMMY_TOKEN_SIGNING_KEY)
    }

    private val USER = User("julia", "", listOf(SimpleGrantedAuthority("ROLE_USER")))
}
