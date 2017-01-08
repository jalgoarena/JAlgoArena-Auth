package com.jalgoarena.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.security.auth.ajax.AjaxAuthenticationProvider
import com.jalgoarena.security.auth.ajax.AjaxLoginProcessingFilter
import com.jalgoarena.security.auth.jwt.JwtAuthenticationProvider
import com.jalgoarena.security.auth.jwt.JwtTokenAuthenticationProcessingFilter
import com.jalgoarena.security.auth.jwt.SkipPathRequestMatcher
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.util.*
import javax.inject.Inject

@Configuration
@EnableWebSecurity
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Inject private lateinit var successHandler: AuthenticationSuccessHandler
    @Inject private lateinit var failureHandler: AuthenticationFailureHandler
    @Inject private lateinit var ajaxAuthenticationProvider: AjaxAuthenticationProvider
    @Inject private lateinit var jwtAuthenticationProvider: JwtAuthenticationProvider
    @Inject private lateinit var authenticationManager: AuthenticationManager
    @Inject private lateinit var objectMapper: ObjectMapper

    @Bean
    open fun buildAjaxLoginProcessingFilter(): AjaxLoginProcessingFilter {
        val filter = AjaxLoginProcessingFilter(LOGIN_ENDPOINT, successHandler, failureHandler, objectMapper)
        filter.setAuthenticationManager(this.authenticationManager)
        return filter
    }

    @Bean
    open fun buildJwtTokenAuthenticationProcessingFilter(): JwtTokenAuthenticationProcessingFilter {
        val pathsToSkip = Arrays.asList(LOGIN_ENDPOINT, SIGNUP_ENDPOINT)
        val matcher = SkipPathRequestMatcher(pathsToSkip, TOKEN_BASED_AUTH_ENTRY_POINT)
        val filter = JwtTokenAuthenticationProcessingFilter(failureHandler, matcher)
        filter.setAuthenticationManager(this.authenticationManager)
        return filter
    }

    @Bean
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(auth: AuthenticationManagerBuilder?) {
        auth!!.authenticationProvider(ajaxAuthenticationProvider)
        auth.authenticationProvider(jwtAuthenticationProvider)
    }

    @Bean
    open fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

    override fun configure(http: HttpSecurity) {
        http
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
                .and()
                    .authorizeRequests()
                    .antMatchers(TOKEN_BASED_AUTH_ENTRY_POINT).authenticated()
                .and()
                    .addFilterBefore(buildAjaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                    .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                    .addFilterBefore(corsFilter(), AjaxLoginProcessingFilter::class.java)
    }

    private fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.addAllowedOrigin("*")
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        source.registerCorsConfiguration("/**", config)

        return CorsFilter(source)
    }

    companion object {
        val JWT_TOKEN_HEADER_PARAM = "X-Authorization"
        val LOGIN_ENDPOINT = "/api/login"
        val SIGNUP_ENDPOINT = "/api/signup"
        val API_USERS_ENTRY_POINT = "/api/users"
        val TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**"
        val ADMIN_ROLE = "ADMIN"
    }
}
