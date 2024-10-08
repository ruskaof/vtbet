package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.itmo.vtbet.model.request.CreateUserRequest
import ru.itmo.vtbet.service.UserService
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `create and get user`() {
        val result = userService.createUser(
            CreateUserRequest(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        val userInDb = userService.getUser(result.id)
        assertEquals(result.id, userInDb.id)
        assertEquals("username", userInDb.username)
        assertEquals("email@email.com", userInDb.email)
        assertNull(userInDb.phoneNumber)
    }

    @Test
    fun `change user balance`() {
        val user = userService.createUser(
            CreateUserRequest(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        userService.addMoneyToUser(user.id, BigDecimal(100))

        val userInDb1 = userService.getUser(user.id)
        assertEquals(BigDecimal(100).toInt(), userInDb1.balanceAmount.toInt())

        userService.subtractMoneyFromUser(user.id, BigDecimal(50))

        val userInDb2 = userService.getUser(user.id)
        assertEquals(BigDecimal(50).toInt(), userInDb2.balanceAmount.toInt())
    }
}
