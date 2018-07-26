package com.jalgoarena.security.auth

import com.jalgoarena.data.UserRepository
import com.jalgoarena.domain.Role
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class JwtUserDetailsService(
        @Autowired private val usersRepository: UserRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): User {
        val user = usersRepository.findByUsername(username).get()
        return User(
                user.username,
                user.password,
                listOf(SimpleGrantedAuthority(Role.valueOf(user.role).authority()))
        )
    }
}
