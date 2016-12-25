package com.jalgoarena

import com.jalgoarena.data.AccountRepository
import com.jalgoarena.domain.Account
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import javax.inject.Inject

@Service
open class AccountUserDetailsService(@Inject val accountRepository: AccountRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val account: Account = accountRepository.findByUsername(username) ?:
                throw UsernameNotFoundException("couldn't find $username!")
        return User(
                account.username,
                account.password,
                true,
                true,
                true,
                true,
                AuthorityUtils.createAuthorityList("ROLE_USER")
        )
    }
}
