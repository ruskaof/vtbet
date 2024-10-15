package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import ru.itmo.vtbet.service.UsersService
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
class UsersServiceIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var usersService: UsersService

    @Test
    fun `create and get user`() {
        val result = usersService.createUser(
            CreateUserRequestDto(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        val userInDb = usersService.getUser(result.id)
        assertEquals(result.id, userInDb.id)
        assertEquals("username", userInDb.username)
        assertEquals("email@email.com", userInDb.email)
        assertNull(userInDb.phoneNumber)
    }

    @Test
    fun `change user balance`() {
        val user = usersService.createUser(
            CreateUserRequestDto(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        usersService.addMoneyToUser(user.id, BigDecimal(100))

        val userInDb1 = usersService.getUser(user.id)
        assertEquals(BigDecimal(100).toInt(), userInDb1.balanceAmount.toInt())

        usersService.subtractMoneyFromUser(user.id, BigDecimal(50))

        val userInDb2 = usersService.getUser(user.id)
        assertEquals(BigDecimal(50).toInt(), userInDb2.balanceAmount.toInt())
    }
}
