package com.jalgoarena

import com.jalgoarena.security.auth.JwtAuthenticationToken
import com.jalgoarena.security.model.UserContext
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController


@SpringBootApplication
@EnableEurekaClient
@EnableConfigurationProperties
@RestController
open class JAlgoArenaAuthServer {

    @GetMapping("/api/user")
    @ResponseBody
    operator fun get(token: JwtAuthenticationToken): UserContext? = token.principal
}

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaAuthServer::class.java, *args)
}
