package com.jalgoarena.security.auth.jwt.extractor

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.stereotype.Component

@Component
open class JwtHeaderTokenExtractor : TokenExtractor {

    override fun extract(payload: String?) = when {
        payload.isNullOrBlank() ->
            throw AuthenticationServiceException("Authorization header cannot be blank!")
        payload!!.length < HEADER_PREFIX.length ->
            throw AuthenticationServiceException("Invalid authorization header size.")
        else ->
            payload.substring(HEADER_PREFIX.length, payload.length)
    }

    companion object {
        var HEADER_PREFIX = "Bearer "
    }
}
