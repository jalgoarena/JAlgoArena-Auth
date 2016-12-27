package com.jalgoarena.common

enum class ErrorCode(val errorCode: Int) {
    GLOBAL(2), AUTHENTICATION(10), JWT_TOKEN_EXPIRED(11)
}
