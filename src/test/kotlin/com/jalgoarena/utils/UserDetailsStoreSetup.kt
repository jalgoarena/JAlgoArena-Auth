package com.jalgoarena.utils

import com.jalgoarena.data.UserDetailsRepository
import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User

class UserDetailsStoreSetup() {

    fun createDb() {

        val repository = UserDetailsRepository()
        try {
            repository.addUser(User(
                    username = "admin",
                    password = "123123",
                    email = "admin@mail.com",
                    region = "Krakow",
                    team = "Admins",
                    role = Role.ADMIN
            ))
        } finally {
            repository.destroy()
        }
    }
}

fun main(args: Array<String>) {
    UserDetailsStoreSetup().createDb()
}

