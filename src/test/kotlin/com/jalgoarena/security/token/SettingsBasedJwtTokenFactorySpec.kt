package com.jalgoarena.security.token

import com.jalgoarena.security.config.JwtSettings
import org.junit.Test
import org.mockito.Mockito
import org.springframework.security.core.userdetails.User

class SettingsBasedJwtTokenFactorySpec {

    private val jwtSettings = Mockito.mock(JwtSettings::class.java)
    private val factory = SettingsBasedJwtTokenFactory(jwtSettings)

    private val DUMMY_PASSWORD = ""
    private val BLANK_USERNAME = " "
    private val USERNAME = "username"

    @Test(expected = IllegalArgumentException::class)
    fun throws_exception_if_username_is_blank() {
        factory.generateToken(User(BLANK_USERNAME, DUMMY_PASSWORD, emptyList()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun throws_exception_if_authorities_are_empty() {
        factory.generateToken(User(USERNAME, DUMMY_PASSWORD, emptyList()))
    }
}
