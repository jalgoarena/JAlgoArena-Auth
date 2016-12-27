package com.jalgoarena.security.model

enum class Scopes {
    REFRESH_TOKEN;

    fun authority(): String = "ROLE_${this.name}"
}
