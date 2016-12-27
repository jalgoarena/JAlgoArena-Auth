package com.jalgoarena.security.auth.jwt

import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.OrRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import javax.servlet.http.HttpServletRequest

class SkipPathRequestMatcher(pathsToSkip: List<String>, processingPath: String) : RequestMatcher {
    private val matchers: OrRequestMatcher
    private val processingMatcher: RequestMatcher

    init {
        val requestMatchers = pathsToSkip.map(::AntPathRequestMatcher)
        matchers = OrRequestMatcher(requestMatchers)
        processingMatcher = AntPathRequestMatcher(processingPath)
    }

    override fun matches(request: HttpServletRequest): Boolean {
        if (matchers.matches(request)) {
            return false
        }
        return processingMatcher.matches(request)
    }
}
