package com.jalgoarena.security.model.token

import com.fasterxml.jackson.annotation.JsonIgnore

import io.jsonwebtoken.Claims

data class AccessJwtToken(
        override val token: String,
        @JsonIgnore val claims: Claims
) : JwtToken
