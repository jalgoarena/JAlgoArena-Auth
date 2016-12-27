package com.jalgoarena.web

import com.jalgoarena.data.UserDetailsRepository
import com.jalgoarena.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
class UsersController {

    @Autowired
    lateinit var repository: UserDetailsRepository

    @GetMapping("/users", produces = arrayOf("application/json"))
    fun users(): List<User> = repository.findAll()

    @GetMapping("/users/{username}", produces = arrayOf("application/json"))
    fun users(@PathVariable username: String) = repository.findByUsername(username)

    @PostMapping("/signup", produces = arrayOf("application/json"))
    fun signup(@RequestBody userDetails: User) = repository.addUser(userDetails)
}
