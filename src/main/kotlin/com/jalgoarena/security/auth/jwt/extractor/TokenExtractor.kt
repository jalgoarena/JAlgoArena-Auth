package com.jalgoarena.security.auth.jwt.extractor

interface TokenExtractor {
    fun extract(payload: String): String
}
