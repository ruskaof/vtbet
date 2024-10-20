package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Test

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import ru.itmo.vtbet.service.ComplexUsersService
import java.math.BigDecimal
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UsersControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var complexUsersService: ComplexUsersService

    @Autowired
    private lateinit var usersService: ComplexUsersService


    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun getUser() {
        val username = UUID.randomUUID().toString()
        val user = complexUsersService.createUser(
            CreateUserRequestDto(
                username,
                "email@email.com",
            )
        )

        mockMvc.perform(get("/users/${user.userId}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.userId))
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.email").value("email@email.com"))
    }

    @Test
    fun createUser() {
        val username = UUID.randomUUID().toString()
        mockMvc.perform(
            post("/users").contentType("application/json")
                .content("""{"username":"$username","email":"email@email.com"}""")
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.username").value(username))
            .andExpect(jsonPath("$.email").value("email@email.com"))
    }

    @Test
    fun deleteUser() {
        val username = UUID.randomUUID().toString()
        val user = complexUsersService.createUser(
            CreateUserRequestDto(
                username,
                "email@email.com",
            )
        )

        mockMvc.perform(delete("/users/${user.userId}"))
            .andExpect(status().isNoContent)

        assertNull(usersService.getUser(user.userId))
    }

    @Test
    fun updateUser() {
        val username = UUID.randomUUID().toString()
        val user = complexUsersService.createUser(
            CreateUserRequestDto(
                username,
                "email@email.com",
            )
        )

        mockMvc.perform(
            put("/users/${user.userId}").contentType("application/json")
                .content("""{"username":"newUsername","email":"newEmail@email.com"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.userId))
            .andExpect(jsonPath("$.username").value("newUsername"))
            .andExpect(jsonPath("$.email").value("newEmail@email.com"))

        val newUser = usersService.getUser(user.userId)
        assertEquals(newUser?.username, "newUsername")
        assertEquals(newUser?.email, "newEmail@email.com")
    }

    @Test
    fun addMoney() {
        val username = UUID.randomUUID().toString()
        val user = complexUsersService.createUser(
            CreateUserRequestDto(
                username,
                "email@email.com",
            )
        )

        mockMvc.perform(
            post("/users/${user.userId}/balance/add").contentType("application/json")
                .content("""{"amount":100}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(user.userId))
            .andExpect(jsonPath("$.username").value(username))

        val newUser = complexUsersService.getUser(user.userId)
        assertEquals(BigDecimal("100.00"), newUser?.balanceAmount)
    }

    @Test
    fun addMoneyWhenUserNotExist() {
        mockMvc.perform(
            post("/users/123/balance/add").contentType("application/json")
                .content("""{"amount":100}""")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun addMoneyWhenAmountIsNegative() {
        val username = UUID.randomUUID().toString()
        val user = complexUsersService.createUser(
            CreateUserRequestDto(
                username,
                "email@email.com",
            )
        )

        mockMvc.perform(
            post("/users/${user.userId}/balance/add").contentType("application/json")
                .content("""{"amount":-100}""")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun deleteUserWhenUserNotExist() {
        mockMvc.perform(delete("/users/123"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun updateUserWhenUserNotExist() {
        mockMvc.perform(
            put("/users/123").contentType("application/json")
                .content("""{"username":"newUsername","email":"newEmail@email.com"}""")
        )
            .andExpect(status().isNotFound)
    }

    @Test
    fun getUserWhenUserNotExist() {
        mockMvc.perform(get("/users/123"))
            .andExpect(status().isNotFound)
    }
}