package com.jalgoarena.security.auth.ajax

import com.jalgoarena.data.UsersRepository
import com.jalgoarena.security.model.UserContext
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import javax.inject.Inject

@Component
open class AjaxAuthenticationProvider(
        @Inject private val usersRepository : UsersRepository,
        @Inject private val encoder: BCryptPasswordEncoder
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.principal as String
        val password = authentication.credentials as String

        val user = usersRepository.findByUsername(username)

        if (!encoder.matches(password, user.password)) {
            throw BadCredentialsException("Authentication Failed. Username or Password not valid.")
        }

        val authorities = arrayOf(user.role)
                .map({ role -> SimpleGrantedAuthority(role.authority()) })

        val userContext = UserContext.create(user.username, authorities)

        return AjaxAuthenticationToken(userContext, null, userContext.authorities)
    }

    override fun supports(authentication: Class<*>) =
            AjaxAuthenticationToken::class.java.isAssignableFrom(authentication)
}
