package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import jetbrains.exodus.entitystore.Entity

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class User(
        val username: String,
        val password: String,
        val email: String,
        val region: String,
        val team: String,
        val role: Role = Role.USER
) {
    companion object {
        fun from(entity: Entity): User {
            return User(
                    entity.getProperty(Constants.username) as String,
                    entity.getProperty(Constants.password) as String,
                    entity.getProperty(Constants.email) as String,
                    entity.getProperty(Constants.region) as String,
                    entity.getProperty(Constants.team) as String,
                    Role.valueOf(entity.getProperty(Constants.role) as String)
            )
        }
    }
}
