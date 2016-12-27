package com.jalgoarena.security.config

import java.util.Arrays

import com.jalgoarena.security.RestAuthenticationEntryPoint
import com.jalgoarena.security.auth.ajax.AjaxAuthenticationProvider
import com.jalgoarena.security.auth.ajax.AjaxLoginProcessingFilter
import com.jalgoarena.security.auth.jwt.JwtTokenAuthenticationProcessingFilter
import com.jalgoarena.security.auth.jwt.SkipPathRequestMatcher
import com.jalgoarena.security.auth.jwt.extractor.TokenExtractor
import org.springframework.beans.factory.annotation.Autowired
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

import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.security.auth.jwt.JwtAuthenticationProvider

@Configuration
@EnableWebSecurity
open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

    @Autowired private lateinit var authenticationEntryPoint: RestAuthenticationEntryPoint
    @Autowired private lateinit var successHandler: AuthenticationSuccessHandler
    @Autowired private lateinit var failureHandler: AuthenticationFailureHandler
    @Autowired private lateinit var ajaxAuthenticationProvider: AjaxAuthenticationProvider
    @Autowired private lateinit var jwtAuthenticationProvider: JwtAuthenticationProvider
    @Autowired private lateinit var tokenExtractor: TokenExtractor
    @Autowired private lateinit var authenticationManager: AuthenticationManager
    @Autowired private lateinit var objectMapper: ObjectMapper

    @Bean
    open fun buildAjaxLoginProcessingFilter(): AjaxLoginProcessingFilter {
        val filter = AjaxLoginProcessingFilter(FORM_BASED_LOGIN_ENTRY_POINT, successHandler, failureHandler, objectMapper)
        filter.setAuthenticationManager(this.authenticationManager)
        return filter
    }

    @Bean
    open fun buildJwtTokenAuthenticationProcessingFilter(): JwtTokenAuthenticationProcessingFilter {
        val pathsToSkip = Arrays.asList(TOKEN_REFRESH_ENTRY_POINT, FORM_BASED_LOGIN_ENTRY_POINT)
        val matcher = SkipPathRequestMatcher(pathsToSkip, TOKEN_BASED_AUTH_ENTRY_POINT)
        val filter = JwtTokenAuthenticationProcessingFilter(failureHandler, tokenExtractor, matcher)
        filter.setAuthenticationManager(this.authenticationManager)
        return filter
    }

    @Bean
    @Throws(Exception::class)
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
                .csrf().disable() // We don't need CSRF for JWT based authentication
                .exceptionHandling()
                .authenticationEntryPoint(this.authenticationEntryPoint)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers(FORM_BASED_LOGIN_ENTRY_POINT).permitAll() // Login end-point
                .antMatchers(TOKEN_REFRESH_ENTRY_POINT).permitAll() // Token refresh end-point
                .antMatchers("/console").permitAll() // H2 Console Dash-board - only for testing
                .and()
                .authorizeRequests()
                .antMatchers(TOKEN_BASED_AUTH_ENTRY_POINT).authenticated() // Protected API End-points
                .and()
                .addFilterBefore(buildAjaxLoginProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(buildJwtTokenAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter::class.java)
    }

    companion object {
        val JWT_TOKEN_HEADER_PARAM = "X-Authorization"
        val FORM_BASED_LOGIN_ENTRY_POINT = "/api/auth/login"
        val TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**"
        val TOKEN_REFRESH_ENTRY_POINT = "/api/auth/token"
    }
}
