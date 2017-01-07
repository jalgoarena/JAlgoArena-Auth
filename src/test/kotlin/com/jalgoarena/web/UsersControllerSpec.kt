package com.jalgoarena.web

import com.fasterxml.jackson.databind.node.ArrayNode
import com.jalgoarena.data.UsersRepository
import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User
import com.jalgoarena.security.auth.ajax.AjaxAuthenticationProvider
import org.hamcrest.Matchers.*
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import javax.inject.Inject

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
class UsersControllerSpec {

    @Inject
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var usersRepository: UsersRepository

    @MockBean
    private lateinit var ajaxAuthenticationProvider: AjaxAuthenticationProvider

    @Test
    fun returns_200_and_users_with_empty_password_and_email_for_get_users() {
        given(usersRepository.findAll()).willReturn(listOf(
                USER_MIKOLAJ, USER_JULIA
        ))

        mockMvc.perform(get("/users")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", hasSize<ArrayNode>(2)))
                .andExpect(jsonPath("$[0].username", `is`(USER_MIKOLAJ.username)))
                .andExpect(jsonPath("$[0].password", `is`("")))
                .andExpect(jsonPath("$[0].email", `is`("")))
                .andExpect(jsonPath("$[0].region", `is`(USER_MIKOLAJ.region)))
                .andExpect(jsonPath("$[0].team", `is`(USER_MIKOLAJ.team)))
                .andExpect(jsonPath("$[0].role", `is`("USER")))
                .andExpect(jsonPath("$[0].id", `is`(USER_MIKOLAJ.id)))
    }

    @Test
    fun returns_200_and_newly_created_user_for_post_signup() {
        val userJuliaWithoutId = USER_JULIA.copy(id = null)
        given(usersRepository.addUser(userJuliaWithoutId)).willReturn(USER_JULIA)

        mockMvc.perform(post("/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(USER_JULIA_JSON))
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.username", `is`(USER_JULIA.username)))
                .andExpect(jsonPath("$.id", `is`(USER_JULIA.id)))
    }

    private val USER_MIKOLAJ = User("mikolaj", "password", "mikolaj@mail.com", "Kraków", "Tyniec Team", Role.USER, "0-0")
    private val USER_JULIA = User("julia", "password1", "julia@mail.com", "Kraków", "Tyniec Team", Role.USER, "0-1")


    @Language("JSON")
    private val USER_JULIA_JSON = """{
  "username": "julia",
  "password": "password1",
  "email": "julia@mail.com",
  "region": "Kraków",
  "team": "Tyniec Team",
  "role": "USER"
}
"""
}
