package com.jalgoarena.security.auth.jwt

import com.jalgoarena.security.auth.JwtAuthenticationToken
import com.jalgoarena.security.config.WebSecurityConfig
import com.jalgoarena.security.model.token.RawAccessJwtToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenAuthenticationProcessingFilter(
        authenticationFailureHandler: AuthenticationFailureHandler,
        matcher: RequestMatcher
) : AbstractAuthenticationProcessingFilter(matcher),
        AuthenticationFailureHandler by authenticationFailureHandler {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {

        val tokenHeader: String? = request.getHeader(WebSecurityConfig.JWT_TOKEN_HEADER_PARAM)

        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            throw AuthenticationServiceException("No JWT token found in request headers")
        }

        val authToken = tokenHeader.substring(7)
        val token = RawAccessJwtToken(authToken)
        return authenticationManager.authenticate(JwtAuthenticationToken(token))
    }

    override fun successfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            chain: FilterChain,
            authResult: Authentication
    ) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
        chain.doFilter(request, response)
    }

    override fun unsuccessfulAuthentication(
            request: HttpServletRequest,
            response: HttpServletResponse,
            failed: AuthenticationException
    ) {
        SecurityContextHolder.clearContext()
        onAuthenticationFailure(request, response, failed)
    }
}
