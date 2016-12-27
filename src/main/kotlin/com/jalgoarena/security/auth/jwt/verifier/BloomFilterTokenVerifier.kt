package com.jalgoarena.security.auth.jwt.verifier

import org.springframework.stereotype.Component

@Component
open class BloomFilterTokenVerifier : TokenVerifier {
    override fun verify(jti: String) = true
}
