package com.jalgoarena.security.model.token


import com.jalgoarena.security.model.Scopes
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import java.util.*

class RefreshToken private constructor(val claims: Jws<Claims>) : JwtToken {

    override fun getToken(): String? {
        return null
    }

    val jti: String
        get() = claims.body.id

    val subject: String
        get() = claims.body.subject

    companion object {

        fun create(token: RawAccessJwtToken, signingKey: String): Optional<RefreshToken> {
            val claims = token.parseClaims(signingKey)

            val scopes = claims.body.get("scopes", Array<String>::class.java)
            if (scopes == null || scopes.isEmpty()
                    || scopes.filter({ scope -> Scopes.REFRESH_TOKEN.authority() == scope }).isEmpty()) {
                return Optional.empty<RefreshToken>()
            }

            return Optional.of(RefreshToken(claims))
        }
    }
}
