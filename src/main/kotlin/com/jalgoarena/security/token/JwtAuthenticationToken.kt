package com.jalgoarena.security.token

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class JwtAuthenticationToken : AbstractAuthenticationToken {

    private var rawAccessToken: JwtToken? = null
    private var userDetails: UserDetails? = null

    constructor(unsafeToken: JwtToken) : super(null) {
        this.rawAccessToken = unsafeToken
        this.isAuthenticated = false
    }

    constructor(userDetails: UserDetails, authorities: Collection<GrantedAuthority>) : super(authorities) {
        this.eraseCredentials()
        this.userDetails = userDetails
        super.setAuthenticated(true)
    }

    override fun setAuthenticated(authenticated: Boolean) {
        if (authenticated) {
            throw IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead")
        }
        super.setAuthenticated(false)
    }

    override fun getCredentials(): JwtToken? {
        return rawAccessToken
    }

    override fun getPrincipal(): UserDetails? {
        return userDetails
    }

    override fun eraseCredentials() {
        super.eraseCredentials()
        this.rawAccessToken = null
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false

        other as JwtAuthenticationToken

        if (rawAccessToken != other.rawAccessToken) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (rawAccessToken?.hashCode() ?: 0)
        return result
    }
}
