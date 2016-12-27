package com.jalgoarena.security.model

import org.springframework.security.core.GrantedAuthority

class UserContext(val username: String, val authorities: List<GrantedAuthority>) {

    companion object {
        fun create(username: String, authorities: List<GrantedAuthority>): UserContext {
            if (username.isBlank()) throw IllegalArgumentException("Username is blank: $username")
            return UserContext(username, authorities)
        }
    }
}
