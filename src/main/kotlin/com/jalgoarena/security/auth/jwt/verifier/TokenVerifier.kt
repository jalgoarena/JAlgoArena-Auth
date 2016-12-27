package com.jalgoarena.security.auth.jwt.verifier

interface TokenVerifier {
    fun verify(jti: String): Boolean
}
