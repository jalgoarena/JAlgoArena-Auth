package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableResourceServer
@EnableAuthorizationServer
open class JAlgoArenaAuthServer {

    @RequestMapping("/user")
    fun user(user: Principal): Principal {
        return user
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaAuthServer::class.java, *args)
}
