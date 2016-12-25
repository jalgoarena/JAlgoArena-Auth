package com.jalgoarena.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import jetbrains.exodus.entitystore.Entity

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserDetails(
        val username: String,
        val password: String,
        val email: String,
        val region: String,
        val team: String
) {
    companion object {
        fun from(entity: Entity): UserDetails {
            return UserDetails(
                    entity.getProperty(Constants.username) as String,
                    entity.getProperty(Constants.password) as String,
                    entity.getProperty(Constants.email) as String,
                    entity.getProperty(Constants.region) as String,
                    entity.getProperty(Constants.team) as String
            )
        }
    }
}
