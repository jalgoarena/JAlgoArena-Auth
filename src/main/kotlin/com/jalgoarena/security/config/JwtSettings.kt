package com.jalgoarena.security.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "jalgoarena.security.jwt")
open class JwtSettings {
    open var tokenExpirationTime: Int? = null
    open var tokenIssuer: String? = null
    open var tokenSigningKey: String? = null
}
