package com.jalgoarena

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import javax.persistence.EntityManagerFactory


@SpringBootApplication
@EnableConfigurationProperties
@EnableTransactionManagement
@Configuration
open class JAlgoArenaAuthApp {

    @Bean
    open fun transactionManager(entityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        val transactionManager = JpaTransactionManager()
        transactionManager.entityManagerFactory = entityManagerFactory
        return transactionManager
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(JAlgoArenaAuthApp::class.java, *args)
}
