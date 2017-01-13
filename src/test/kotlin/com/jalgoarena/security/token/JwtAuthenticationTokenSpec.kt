package com.jalgoarena.security.token

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.mockito.Mockito

class JwtAuthenticationTokenSpec {

    private val jwtToken = Mockito.mock(JwtToken::class.java)

    @Test(expected = IllegalArgumentException::class)
    fun throws_exception_when_setting_authenticated_flag_by_api() {
        JwtAuthenticationToken(jwtToken).isAuthenticated = true
    }

    @Test
    fun two_tokens_with_same_values_are_equal() {
        val token1 = JwtAuthenticationToken(jwtToken)
        val token2 = JwtAuthenticationToken(jwtToken)

        assertThat(token1).isEqualTo(token2)
        assertThat(token1.hashCode()).isEqualTo(token2.hashCode())
    }
}
