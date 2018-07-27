package com.jalgoarena.domain

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jalgoarena.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.Before
import org.junit.Test
import org.springframework.boot.test.json.JacksonTester

class UserJsonSerializationTest {

    private lateinit var json: JacksonTester<User>

    @Before
    fun setup() {
        val objectMapper = jacksonObjectMapper()
        JacksonTester.initFields(this, objectMapper)
    }

    @Test
    fun should_serialize_user() {
        assertThat(json.write(USER))
                .isEqualToJson("user.json")
    }

    @Test
    fun should_deserialize_user() {
        assertThat(json.parse(USER_JSON))
                .isEqualTo(USER)
    }

    companion object {
        private val USER = User(
                username = "mikolaj",
                password = "blabla",
                firstname = "Mikolaj",
                surname = "Spolnik",
                email = "mikolaj@email.com",
                region = "Kraków",
                team = "Tyniec Team",
                role = Role.USER.toString(),
                id = 0
        )

        @Language("JSON")
        private val USER_JSON = """{
  "username": "mikolaj",
  "password": "blabla",
  "firstname": "Mikolaj",
  "surname": "Spolnik",
  "email": "mikolaj@email.com",
  "region": "Kraków",
  "team": "Tyniec Team",
  "role": "USER",
  "id": 0
}
"""

    }
}
