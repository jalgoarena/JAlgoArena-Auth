package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration

@SpringBootApplication
@EnableConfigurationProperties
@EnableCaching
@Configuration
open class JAlgoArenaAuthApp

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaAuthApp::class.java, *args)
}
