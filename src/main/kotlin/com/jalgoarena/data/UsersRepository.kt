package com.jalgoarena.data

import com.jalgoarena.domain.User

interface UsersRepository {
    fun findAll(): List<User>
    fun findByUsername(username: String): User
    fun destroy()
    fun add(user: User): User
    fun update(user: User): User
}
