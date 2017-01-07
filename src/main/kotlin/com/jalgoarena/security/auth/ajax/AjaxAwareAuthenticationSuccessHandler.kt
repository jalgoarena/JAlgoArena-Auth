package com.jalgoarena.security.auth.ajax

import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.security.model.UserContext
import com.jalgoarena.security.model.token.JwtTokenFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.WebAttributes
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
open class AjaxAwareAuthenticationSuccessHandler(
        @Inject private val mapper: ObjectMapper,
        @Inject private val tokenFactory: JwtTokenFactory
) : AuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
            request: HttpServletRequest,
            response: HttpServletResponse,
            authentication: Authentication
    ) {

        val userContext = authentication.principal as UserContext

        val (token) = tokenFactory.createAccessJwtToken(userContext)

        val tokenMap = mapOf(Pair("token", token))

        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.writer, tokenMap)

        clearAuthenticationAttributes(request)
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     */
    private fun clearAuthenticationAttributes(request: HttpServletRequest) {
        val session = request.getSession(false) ?: return

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION)
    }
}
