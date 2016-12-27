package com.jalgoarena.security

import com.jalgoarena.domain.User

interface UserService {
    fun findByUsername(username: String): User?
}
