package com.jalgoarena.security.auth.ajax

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority

class AjaxAuthenticationToken(val username: Any, val password: Any?, authorities: List<GrantedAuthority>? = null)
    : UsernamePasswordAuthenticationToken(username, password, authorities) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false

        other as AjaxAuthenticationToken

        if (username != other.username) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + username.hashCode()
        return result
    }
}
