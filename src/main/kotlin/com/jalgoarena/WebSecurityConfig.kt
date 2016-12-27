package com.jalgoarena

import com.jalgoarena.data.UserDetailsRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Configuration
@EnableWebSecurity
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Bean
    open fun accountUserDetailsService(repository: UserDetailsRepository) =
            AccountUserDetailsService(repository)

    @Bean
    open fun passwordEncoder() = BCryptPasswordEncoder()

    override fun configure(http: HttpSecurity) {
        http
                .formLogin()
                    .loginPage("/login")
                    .permitAll()
                .and()
                    .requestMatchers()
                        .antMatchers("/login", "/oauth/authorize", "/oauth/confirm_access", "signup")
                .and()
                    .authorizeRequests().anyRequest().authenticated()
    }
}
