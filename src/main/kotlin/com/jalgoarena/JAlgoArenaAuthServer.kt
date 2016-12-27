package com.jalgoarena

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import java.security.Principal


@SpringBootApplication
@EnableEurekaClient
@RestController
@EnableResourceServer
@EnableAuthorizationServer
open class JAlgoArenaAuthServer : WebMvcConfigurerAdapter() {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @RequestMapping("/user")
    fun user(user: Principal?): Principal? {
        log.info("/user has been called")
        log.debug("user info: ${user.toString()}")
        return user
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaAuthServer::class.java, *args)
}
