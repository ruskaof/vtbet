package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import ru.itmo.vtbet.model.request.CreateUserRequest
import ru.itmo.vtbet.service.UserService
import java.math.BigDecimal

@SpringBootTest
class UserServiceIntegrationTest : BaseIntegrationTest {

    @Autowired
    private lateinit var userService: UserService

    @Test
    fun `create and get user`() {
        // when
        val result = userService.createUser(
            CreateUserRequest(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        // then
        val userInDb = userService.getUser(result.id)
        assertEquals(result.id, userInDb.id)
        assertEquals("username", userInDb.username)
        assertEquals("email@email.com", userInDb.email)
        assertNull(userInDb.phoneNumber)
    }

    @Test
    fun `change user balance`() {
        // given
        val user = userService.createUser(
            CreateUserRequest(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        // when
        userService.addMoneyToUser(user.id, BigDecimal(100))

        // then
        val userInDb1 = userService.getUser(user.id)
        assertEquals(BigDecimal(100).toInt(), userInDb1.balanceAmount.toInt())

        // when
        userService.subtractMoneyFromUser(user.id, BigDecimal(50))

        // then
        val userInDb2 = userService.getUser(user.id)
        assertEquals(BigDecimal(50).toInt(), userInDb2.balanceAmount.toInt())
    }
}