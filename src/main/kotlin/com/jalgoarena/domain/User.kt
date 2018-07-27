package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import javax.persistence.*

@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
        @Id @GeneratedValue(strategy = GenerationType.AUTO)
        var id: Int? = null,
        @Column(unique=true, nullable=false)
        var username: String = "",
        @Column(nullable=false)
        var firstname: String = "",
        @Column(nullable=false)
        var surname: String = "",
        @Column(nullable=false)
        var password: String = "",
        @Column(unique=true, nullable=false)
        var email: String = "",
        @Column(nullable=false)
        var region: String = "",
        @Column(nullable=false)
        var team: String = "",
        @Column(nullable=false)
        var role: String = Role.USER.toString()
)