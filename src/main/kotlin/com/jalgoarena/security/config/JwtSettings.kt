package com.jalgoarena.security.config

import com.jalgoarena.security.model.token.JwtToken
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jalgoarena.security.jwt")
open class JwtSettings {
    /**
     * [JwtToken] will expire after this time.
     */
    var tokenExpirationTime: Int? = null

    /**
     * Token issuer.
     */
    var tokenIssuer: String? = null

    /**
     * Key is used to sign [JwtToken].
     */
    var tokenSigningKey: String? = null

    /**
     * [JwtToken] can be refreshed during this timeframe.
     */
    var refreshTokenExpTime: Int? = null
}
