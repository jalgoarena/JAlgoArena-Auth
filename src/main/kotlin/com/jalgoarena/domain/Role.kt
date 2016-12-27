package com.jalgoarena.domain

enum class Role {
    ADMIN, USER;

    fun authority() = "ROLE_$name"
}
