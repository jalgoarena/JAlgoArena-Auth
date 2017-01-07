package com.jalgoarena.security.auth.jwt

import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

class SkipPathRequestMatcher(pathsToSkip: List<String>, processingPath: String) : RequestMatcher {
    private val matchers: OrRequestMatcher
    private val processingMatcher: RequestMatcher = AntPathRequestMatcher(processingPath)

    init {
        val requestMatchers = pathsToSkip.map(::AntPathRequestMatcher)
        matchers = OrRequestMatcher(requestMatchers)
    }

    override fun matches(request: HttpServletRequest): Boolean {
        return if (matchers.matches(request)) false else processingMatcher.matches(request)
    }
}
