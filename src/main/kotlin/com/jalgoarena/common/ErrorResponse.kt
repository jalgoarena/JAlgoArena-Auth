package com.jalgoarena.common

import java.util.Date

import org.springframework.http.HttpStatus

class ErrorResponse(
        val message: String,
        val errorCode: ErrorCode,
        private val status: HttpStatus) {

    val timestamp: Date

    init {
        this.timestamp = java.util.Date()
    }

    fun getStatus(): Int? {
        return status.value()
    }
}
