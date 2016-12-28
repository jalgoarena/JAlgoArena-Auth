package com.jalgoarena.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.security.RestAuthenticationEntryPoint
import com.jalgoarena.security.auth.ajax.AjaxAuthenticationProvider
import com.jalgoarena.security.auth.ajax.AjaxLoginProcessingFilter
import com.jalgoarena.security.auth.jwt.JwtAuthenticationProvider
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
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import java.util.*


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
                .authenticationEntryPoint(this.authenticationEntryPoint)
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                    .authorizeRequests()
                    .antMatchers(FORM_BASED_LOGIN_ENTRY_POINT).permitAll()
                    .antMatchers(TOKEN_REFRESH_ENTRY_POINT).permitAll()
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
        val FORM_BASED_LOGIN_ENTRY_POINT = "/api/auth/login"
        val TOKEN_REFRESH_ENTRY_POINT = "/api/auth/token"
        val TOKEN_BASED_AUTH_ENTRY_POINT = "/api/**"
    }
}
