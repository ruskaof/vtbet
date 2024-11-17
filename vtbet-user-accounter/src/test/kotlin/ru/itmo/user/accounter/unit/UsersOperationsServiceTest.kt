package ru.itmo.user.accounter.unit


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import reactor.core.publisher.Mono
import ru.itmo.common.dto.UserDto
import ru.itmo.common.dto.UserWithPasswordDto
import ru.itmo.common.entity.RolesEntity
import ru.itmo.common.entity.UsersEntity
import ru.itmo.user.accounter.repository.UsersRepository
import ru.itmo.user.accounter.service.UsersOperationsService
import java.time.Instant


class UsersOperationsServiceTest {

    private val usersRepository = mock(UsersRepository::class.java)
    private val usersOperationsService = UsersOperationsService(usersRepository)


    @Test
    fun `get user`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val usersEntityDto = UsersEntity(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf(RolesEntity(1L, "USER")),
        )
        `when`(usersRepository.findById(userId)).thenReturn(Mono.just(usersEntityDto))


        val result = usersOperationsService.getUser(userId).block()

        val expectedResult = UserDto(
            userId = userId,
            username = username,
            roles = setOf("USER")
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `get user by username`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val usersEntityDto = UsersEntity(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf(RolesEntity(1L, "USER")),
        )
        `when`(usersRepository.findByUsername(username)).thenReturn(Mono.just(usersEntityDto))


        val result = usersOperationsService.getByUserName(username).block()

        val expectedResult = UserDto(
            userId = userId,
            username = username,
            roles = setOf("USER")
        )

        assertEquals(expectedResult, result)
    }

    @Test
    fun `save user`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val usersEntityDto = UsersEntity(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf(RolesEntity(1L, "USER")),
        )
        val userDto = UserWithPasswordDto(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf("USER")
        )

        val expected = UserDto(
            userId = userId,
            username = username,
            roles = setOf("USER")
        )

        `when`(usersRepository.save(any())).thenReturn(Mono.just(usersEntityDto))

        val result = usersOperationsService.save(userDto).block()

        assertEquals(expected, result)
    }

    @Test
    fun `update user`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val usersEntityDto = UsersEntity(
            userId = userId,
            username = username,
            password = "1234",
            roles = setOf(RolesEntity(1L, "USER")),
        )
        val userDto = UserDto(
            userId = userId,
            username = username,
            roles = setOf("USER")
        )

        `when`(usersRepository.save(any())).thenReturn(Mono.just(usersEntityDto))

        val result = usersOperationsService.update(userDto).block()

        assertEquals(userDto, result)
    }

    @Test
    fun `delete by id`() {
        val userId = 1L

        `when`(usersRepository.deleteById(userId)).thenReturn(Mono.empty())

        usersOperationsService.deleteById(userId).block()

        verify(usersRepository).deleteById(userId)
    }
}
