package com.jalgoarena.web

import com.fasterxml.jackson.databind.node.ArrayNode
import com.jalgoarena.data.UsersRepository
import com.jalgoarena.domain.Role
import com.jalgoarena.domain.User
import com.jalgoarena.security.auth.JwtAuthenticationToken
import com.jalgoarena.security.auth.ajax.AjaxAuthenticationProvider
import com.jalgoarena.security.auth.jwt.JwtAuthenticationProvider
import com.jalgoarena.security.auth.jwt.extractor.JwtHeaderTokenExtractor
import com.jalgoarena.security.model.UserContext
import com.jalgoarena.security.model.token.RawAccessJwtToken
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.hasSize
import org.intellij.lang.annotations.Language
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
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

    @MockBean
    private lateinit var jwtAuthenticationProvider: JwtAuthenticationProvider

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

    @Test
    fun returns_401_if_user_did_not_logged_in_for_get_api_user() {
        mockMvc.perform(get("/api/user")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized)
    }

    @Test
    fun returns_200_and_user_details_if_user_is_logged_in_for_get_api_user() {
        given(usersRepository.findByUsername(USER_MIKOLAJ.username)).willReturn(USER_MIKOLAJ)

        given(jwtAuthenticationProvider.supports(JwtAuthenticationToken::class.java)).willReturn(true)
        val token = RawAccessJwtToken(JwtHeaderTokenExtractor().extract(DUMMY_TOKEN))
        given(jwtAuthenticationProvider.authenticate(JwtAuthenticationToken(token))).willReturn(
                JwtAuthenticationToken(
                        UserContext(
                            USER_MIKOLAJ.username,
                            listOf(SimpleGrantedAuthority(Role.USER.authority()))
                        ),
                        listOf(SimpleGrantedAuthority(Role.USER.authority()))
                )
        )

        mockMvc.perform(get("/api/user")
                .header("X-Authorization", DUMMY_TOKEN)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.username", `is`(USER_MIKOLAJ.username)))
                .andExpect(jsonPath("$.password", `is`("")))
                .andExpect(jsonPath("$.email", `is`(USER_MIKOLAJ.email)))
                .andExpect(jsonPath("$.region", `is`(USER_MIKOLAJ.region)))
                .andExpect(jsonPath("$.team", `is`(USER_MIKOLAJ.team)))
                .andExpect(jsonPath("$.role", `is`("USER")))
                .andExpect(jsonPath("$.id", `is`(USER_MIKOLAJ.id)))
    }

    private val USER_MIKOLAJ =
            User("mikolaj", "password", "mikolaj@mail.com", "Kraków", "Tyniec Team", Role.USER, "0-0")
    private val USER_JULIA =
            User("julia", "password1", "julia@mail.com", "Kraków", "Tyniec Team", Role.USER, "0-1")

    private val DUMMY_TOKEN = "Bearer 123j12n31lkmdp012j21d"

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
