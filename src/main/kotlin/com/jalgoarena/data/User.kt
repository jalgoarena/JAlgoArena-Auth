package com.jalgoarena.data

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.jalgoarena.domain.Role
import java.lang.Long.parseLong
import javax.persistence.*

@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        val id: Int? = null,
        val username: String,
        var password: String,
        val email: String,
        val region: String,
        val team: String,
        val role: String
)