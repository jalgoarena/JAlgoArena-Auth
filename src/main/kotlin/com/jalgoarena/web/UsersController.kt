package com.jalgoarena.web

import com.jalgoarena.data.UserDetailsRepository
import com.jalgoarena.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class UsersController {

    @Autowired
    lateinit var repository: UserDetailsRepository

    @GetMapping("/api/users", produces = arrayOf("application/json"))
    fun users(): List<User> {
        return repository.findAll().map { User(
                it.username,
                "",
                it.email,
                it.region,
                it.team,
                it.role,
                it.id
        ) }
    }

    @GetMapping("/api/users/{username}", produces = arrayOf("application/json"))
    fun users(@PathVariable username: String): User? {
        val user = repository.findByUsername(username)

        if (user != null) {
            user.password = ""
        }

        return user
    }

    @PostMapping("/api/auth/signup", produces = arrayOf("application/json"))
    fun signup(@RequestBody userDetails: User) = repository.addUser(userDetails)
}
