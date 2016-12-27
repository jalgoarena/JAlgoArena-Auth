package com.jalgoarena

import com.jalgoarena.data.UserDetailsRepository
import com.jalgoarena.security.UserService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
open class DatabaseUserService(
        @Inject private val userDetailsRepository: UserDetailsRepository) : UserService {

    override fun findByUsername(username: String) = userDetailsRepository.findByUsername(username) ?:
            throw UsernameNotFoundException("User not found: $username")
}
