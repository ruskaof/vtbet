package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.itmo.vtbet.model.request.CreateUserRequestDto
import ru.itmo.vtbet.service.ComplexUsersService
import ru.itmo.vtbet.service.UsersAccountsService
import ru.itmo.vtbet.service.UsersService
import java.math.BigDecimal

@SpringBootTest
@ActiveProfiles("test")
class UsersServiceIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var usersAccountsService: UsersAccountsService

    @Autowired
    private lateinit var complexUsersService: ComplexUsersService

    @Autowired
    private lateinit var usersService: UsersService

    @Test
    fun `create and get user`() {
        val result = complexUsersService.createUser(
            CreateUserRequestDto(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        val userInDb = usersService.getUser(result.userId)
        assertEquals(result.userId, userInDb!!.userId)
        assertEquals("username", userInDb.username)
        assertEquals("email@email.com", userInDb.email)
        assertNull(userInDb.phoneNumber)
    }

    @Test
    fun `change user balance`() {
        val user = complexUsersService.createUser(
            CreateUserRequestDto(
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
            )
        )

        complexUsersService.addMoneyToUser(user.userId, BigDecimal(100))

        val userAccountInDb1 = usersAccountsService.getUserAccount(user.userId)
        assertEquals(BigDecimal(100).toInt(), userAccountInDb1!!.balanceAmount.toInt())

        complexUsersService.subtractMoneyFromUser(user.userId, BigDecimal(50))

        val userAccountInDb2 = usersAccountsService.getUserAccount(user.userId)
        assertEquals(BigDecimal(50).toInt(), userAccountInDb2!!.balanceAmount.toInt())
    }
}
