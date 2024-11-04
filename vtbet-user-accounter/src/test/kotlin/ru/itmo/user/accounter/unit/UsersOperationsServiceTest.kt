package ru.itmo.user.accounter.unit


import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import ru.itmo.user.accounter.model.dto.UserDto
import ru.itmo.user.accounter.model.entity.UsersEntity
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
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        `when`(usersRepository.findById(userId)).thenReturn(Mono.just(usersEntity))


        val result = usersOperationsService.getUser(userId).block()

        val expectedResult = UserDto(
            userId = userId,
            registrationDate = registrationDate,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
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
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        `when`(usersRepository.findByUsername(username)).thenReturn(Mono.just(usersEntity))


        val result = usersOperationsService.getByUserName(username).block()

        val expectedResult = UserDto(
            userId = userId,
            registrationDate = registrationDate,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
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
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        val userDto = UserDto(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        `when`(usersRepository.save(any())).thenReturn(Mono.just(usersEntity))

        val result = usersOperationsService.save(userDto).block()

        assertEquals(userDto, result)
    }

    @Test
    fun `update user`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val usersEntity = UsersEntity(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        val userDto = UserDto(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )

        `when`(usersRepository.save(any())).thenReturn(Mono.just(usersEntity))

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
