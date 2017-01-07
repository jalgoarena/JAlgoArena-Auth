package com.jalgoarena.security.auth.ajax

import com.fasterxml.jackson.databind.ObjectMapper
import com.jalgoarena.common.ErrorCode
import com.jalgoarena.common.ErrorResponse
import com.jalgoarena.security.exceptions.AuthMethodNotSupportedException
import com.jalgoarena.security.exceptions.JwtExpiredTokenException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
open class AjaxAwareAuthenticationFailureHandler(
        @Inject private val mapper: ObjectMapper
) : AuthenticationFailureHandler {

    override fun onAuthenticationFailure(
            request: HttpServletRequest,
            response: HttpServletResponse,
            e: AuthenticationException
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        when (e) {
            is BadCredentialsException, is UsernameNotFoundException -> invalidUserNameOrPassword(response)
            is JwtExpiredTokenException -> tokenHasExpired(response)
            is AuthMethodNotSupportedException -> authMethodNotSupported(e, response)
            else -> authenticationFailed(response)
        }
    }

    private fun authenticationFailed(response: HttpServletResponse) {
        mapper.writeValue(
                response.writer,
                ErrorResponse(
                        "Authentication failed",
                        ErrorCode.AUTHENTICATION,
                        HttpStatus.UNAUTHORIZED
                )
        )
    }

    private fun authMethodNotSupported(e: AuthenticationException, response: HttpServletResponse) {
        mapper.writeValue(
                response.writer,
                ErrorResponse(
                        e.message!!,
                        ErrorCode.AUTHENTICATION,
                        HttpStatus.UNAUTHORIZED
                )
        )
    }

    private fun tokenHasExpired(response: HttpServletResponse) {
        mapper.writeValue(
                response.writer,
                ErrorResponse(
                        "Token has expired",
                        ErrorCode.JWT_TOKEN_EXPIRED,
                        HttpStatus.UNAUTHORIZED
                )
        )
    }

    private fun invalidUserNameOrPassword(response: HttpServletResponse) {
        mapper.writeValue(
                response.writer,
                ErrorResponse(
                        "Invalid username or password",
                        ErrorCode.AUTHENTICATION,
                        HttpStatus.UNAUTHORIZED
                )
        )
    }
}
