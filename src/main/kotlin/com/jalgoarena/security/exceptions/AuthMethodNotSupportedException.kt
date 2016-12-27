package com.jalgoarena.security.exceptions

import org.springframework.security.authentication.AuthenticationServiceException

class AuthMethodNotSupportedException(message: String) :
        AuthenticationServiceException(message)
