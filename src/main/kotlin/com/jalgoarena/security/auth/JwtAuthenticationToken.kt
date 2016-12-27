package com.jalgoarena.security.auth

import com.jalgoarena.security.model.UserContext
import com.jalgoarena.security.model.token.RawAccessJwtToken
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class JwtAuthenticationToken : AbstractAuthenticationToken {

    private var rawAccessToken: RawAccessJwtToken? = null
    private var userContext: UserContext? = null

    constructor(unsafeToken: RawAccessJwtToken) : super(null) {
        this.rawAccessToken = unsafeToken
        this.isAuthenticated = false
    }

    constructor(userContext: UserContext, authorities: Collection<GrantedAuthority>) : super(authorities) {
        this.eraseCredentials()
        this.userContext = userContext
        super.setAuthenticated(true)
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if (authenticated) {
            throw IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead")
        }
        super.setAuthenticated(false)
    }

    override fun getCredentials(): RawAccessJwtToken? {
        return rawAccessToken
    }

    override fun getPrincipal(): UserContext? {
        return userContext
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        this.rawAccessToken = null
    }
}
