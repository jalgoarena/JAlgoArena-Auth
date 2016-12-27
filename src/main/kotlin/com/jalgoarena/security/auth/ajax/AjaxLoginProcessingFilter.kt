package com.jalgoarena.security.auth.ajax

import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.common.WebUtil
import com.jalgoarena.security.exceptions.AuthMethodNotSupportedException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AjaxLoginProcessingFilter(
        defaultProcessUrl: String,
        private val authenticationSuccessHandler: AuthenticationSuccessHandler,
        private val authenticationFailureHandler: AuthenticationFailureHandler,
        private val objectMapper: ObjectMapper
) : AbstractAuthenticationProcessingFilter(defaultProcessUrl) {

    private val LOG = LoggerFactory.getLogger(this.javaClass)

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        validateHttpMethod(request)

        val (username, password) = parseLoginRequest(request)
        val token = UsernamePasswordAuthenticationToken(username, password)

        return this.authenticationManager.authenticate(token)
    }

    private fun parseLoginRequest(request: HttpServletRequest): Pair<String, String> {
        val loginRequest: LoginRequest
        try {
            loginRequest = objectMapper.readValue(request.reader, LoginRequest::class.java)
        } catch(e: JsonMappingException) {
            throw AuthenticationServiceException("Username or Password not provided")
        }

        if (loginRequest.username.isNullOrBlank() || loginRequest.password.isNullOrBlank()) {
            throw AuthenticationServiceException("Username or Password not provided")
        }
        return Pair(loginRequest.username, loginRequest.password)
    }

    private fun validateHttpMethod(request: HttpServletRequest) {
        val isCorrectHttpMethod = arrayOf(
                HttpMethod.POST.name, HttpMethod.OPTIONS.name
        ).contains(request.method)

        if (isCorrectHttpMethod && WebUtil.isAjax(request))
            return

        LOG.debug("Authentication method not supported. Request method: ${request.method}")
        throw AuthMethodNotSupportedException("Authentication method not supported: ${request.method}")
    }

    override fun successfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            chain: FilterChain,
            authResult: Authentication
    ) {
        authenticationSuccessHandler.onAuthenticationSuccess(request, response, authResult)
    }

    override fun unsuccessfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            failed: AuthenticationException
    ) {
        SecurityContextHolder.clearContext()
        authenticationFailureHandler.onAuthenticationFailure(request, response, failed)
    }
}
