package com.jalgoarena.security.endpoint

import com.jalgoarena.data.UsersRepository
import com.jalgoarena.security.auth.jwt.extractor.TokenExtractor
import com.jalgoarena.security.auth.jwt.verifier.TokenVerifier
import com.jalgoarena.security.config.JwtSettings
import com.jalgoarena.security.config.WebSecurityConfig
import com.jalgoarena.security.exceptions.InvalidJwtToken
import com.jalgoarena.security.model.UserContext
import com.jalgoarena.security.model.token.JwtToken
import com.jalgoarena.security.model.token.JwtTokenFactory
import com.jalgoarena.security.model.token.RawAccessJwtToken
import com.jalgoarena.security.model.token.RefreshToken
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import javax.inject.Inject
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class RefreshTokenEndpoint {
    @Inject lateinit private var tokenFactory: JwtTokenFactory
    @Inject lateinit private var jwtSettings: JwtSettings
    @Inject lateinit private var usersRepository: UsersRepository
    @Inject lateinit private var tokenVerifier: TokenVerifier
    @Inject lateinit @Qualifier("jwtHeaderTokenExtractor") private var tokenExtractor: TokenExtractor

    @GetMapping("/api/auth/token", produces = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @ResponseBody
    fun refreshToken(request: HttpServletRequest, response: HttpServletResponse): JwtToken {
        val tokenPayload = tokenExtractor.extract(request.getHeader(WebSecurityConfig.JWT_TOKEN_HEADER_PARAM))

        val rawToken = RawAccessJwtToken(tokenPayload)
        val refreshToken = RefreshToken.create(rawToken, jwtSettings.tokenSigningKey!!)
                .orElseThrow { InvalidJwtToken() }

        val jti = refreshToken.jti()
        if (!tokenVerifier.verify(jti)) {
            throw InvalidJwtToken()
        }

        val subject = refreshToken.subject()
        val user = usersRepository.findByUsername(subject)

        val authorities = arrayOf(user.role)
                .map({ authority -> SimpleGrantedAuthority(authority.authority()) })

        val userContext = UserContext.create(user.username, authorities)
        return tokenFactory.createAccessJwtToken(userContext)
    }
}
