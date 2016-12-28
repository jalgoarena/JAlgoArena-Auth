package com.jalgoarena.security.exceptions

import org.springframework.security.core.AuthenticationException

class AuthMethodNotSupportedException(message: String) :
        AuthenticationException(message)
