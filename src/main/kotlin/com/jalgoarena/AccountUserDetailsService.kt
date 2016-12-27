package com.jalgoarena

import com.jalgoarena.data.UserDetailsRepository
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
open class AccountUserDetailsService(
        @Inject private val userDetailsRepository: UserDetailsRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val userDetails: com.jalgoarena.domain.UserDetails =
                userDetailsRepository.findByUsername(username) ?:
                throw UsernameNotFoundException("couldn't find $username!")

        return User(
                userDetails.username,
                userDetails.password,
                true,
                true,
                true,
                true,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        )
    }
}
