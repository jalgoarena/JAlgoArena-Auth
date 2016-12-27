package com.jalgoarena.security.auth.jwt

import com.jalgoarena.security.auth.JwtAuthenticationToken
import com.jalgoarena.security.auth.jwt.extractor.TokenExtractor
import com.jalgoarena.security.config.WebSecurityConfig
import com.jalgoarena.security.model.token.RawAccessJwtToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenAuthenticationProcessingFilter
@Autowired
constructor(private val authenticationFailureHandler: AuthenticationFailureHandler,
            private val tokenExtractor: TokenExtractor, matcher: RequestMatcher) : AbstractAuthenticationProcessingFilter(matcher) {

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val tokenPayload: String? = request.getHeader(WebSecurityConfig.JWT_TOKEN_HEADER_PARAM)
        val token = RawAccessJwtToken(tokenExtractor.extract(tokenPayload))
        return authenticationManager.authenticate(JwtAuthenticationToken(token))
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain?,
                                          authResult: Authentication) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)
        chain!!.doFilter(request, response)
    }

    override fun unsuccessfulAuthentication(request: HttpServletRequest, response: HttpServletResponse,
                                            failed: AuthenticationException) {
        SecurityContextHolder.clearContext()
        authenticationFailureHandler.onAuthenticationFailure(request, response, failed)
    }
}
