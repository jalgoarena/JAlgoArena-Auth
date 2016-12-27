package com.jalgoarena.utils

import com.jalgoarena.data.UserDetailsRepository
import com.jalgoarena.domain.UserDetails

class UserDetailsStoreSetup() {

    fun createDb() {

        val repository = UserDetailsRepository()
        try {
            repository.addUser(UserDetails(
                    username = "admin",
                    password = "<PASSWORD>",
                    email = "admin@mail.com",
                    region = "Krakow",
                    team = "Admins"

            ))
        } finally {
            repository.destroy()
        }
    }
}

fun main(args: Array<String>) {
    UserDetailsStoreSetup().createDb()
}

