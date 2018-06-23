package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.client.discovery.EnableDiscoveryClient


@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
open class JAlgoArenaAuthApp

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaAuthApp::class.java, *args)
}
