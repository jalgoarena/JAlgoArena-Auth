package com.jalgoarena.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jalgoarena.security.jwt")
open class JwtSettings {
    var tokenExpirationTime: Int? = null

    var tokenIssuer: String? = null

    var tokenSigningKey: String? = null
}
