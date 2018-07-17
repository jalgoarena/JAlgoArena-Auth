package com.jalgoarena.security.config

import com.jalgoarena.security.auth.JwtAuthenticationProvider
import com.jalgoarena.security.auth.JwtAuthenticationTokenFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableWebSecurity
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired
    private lateinit var userDetailsService: UserDetailsService

    @Autowired
    private lateinit var authenticationProvider: JwtAuthenticationProvider

    @Bean
    public override fun authenticationManager(): AuthenticationManager {
        val daoAuthenticationProvider = DaoAuthenticationProvider()
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder())
        daoAuthenticationProvider.setUserDetailsService(userDetailsService)

        return ProviderManager(listOf(authenticationProvider, daoAuthenticationProvider))
    }

    @Bean
    open fun authenticationTokenFilterBean(): JwtAuthenticationTokenFilter {
        val filter = JwtAuthenticationTokenFilter(AntPathRequestMatcher(TOKEN_BASED_AUTH_ENTRY_POINT))
        filter.setAuthenticationManager(authenticationManager())
        return filter
    }

    override fun configure(authenticationManagerBuilder: AuthenticationManagerBuilder) {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
    }

    @Bean
    open fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(httpSecurity: HttpSecurity) {
        httpSecurity
                .csrf().disable()
                .exceptionHandling()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers(LOGIN_ENDPOINT).permitAll()
                    .antMatchers(SIGNUP_ENDPOINT).permitAll()
                    .antMatchers(API_USERS_ENTRY_POINT).hasRole(ADMIN_ROLE)
                    .antMatchers(TOKEN_BASED_AUTH_ENTRY_POINT).authenticated()

        addCustomFilters(httpSecurity)
        disablePageCaching(httpSecurity)
    }

    private fun addCustomFilters(httpSecurity: HttpSecurity) {
        httpSecurity
                .addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter::class.java)
    }

    private fun disablePageCaching(httpSecurity: HttpSecurity) {
        httpSecurity.headers().cacheControl()
    }

    companion object {
        const val JWT_TOKEN_HEADER_PARAM = "X-Authorization"
        const val LOGIN_ENDPOINT = "/login"
        const val SIGNUP_ENDPOINT = "/signup"
        const val API_USERS_ENTRY_POINT = "/api/users"
        const val TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**"
        const val ADMIN_ROLE = "ADMIN"
    }
}
