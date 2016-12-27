package com.jalgoarena.common

import javax.servlet.http.HttpServletRequest
import org.springframework.security.web.savedrequest.SavedRequest

object WebUtil {
    private val XML_HTTP_REQUEST = "XMLHttpRequest"
    private val X_REQUESTED_WITH = "X-Requested-With"

    private val CONTENT_TYPE = "Content-type"
    private val CONTENT_TYPE_JSON = "application/json"

    fun isAjax(request: HttpServletRequest): Boolean {
        return XML_HTTP_REQUEST == request.getHeader(X_REQUESTED_WITH)
    }

    fun isAjax(request: SavedRequest): Boolean {
        return request.getHeaderValues(X_REQUESTED_WITH).contains(XML_HTTP_REQUEST)
    }

    fun isContentTypeJson(request: SavedRequest): Boolean {
        return request.getHeaderValues(CONTENT_TYPE).contains(CONTENT_TYPE_JSON)
    }
}
