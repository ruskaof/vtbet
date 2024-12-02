package ru.itmo.user.accounter.unit

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import ru.itmo.common.dto.FileDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.UpdateUserRequestDto
import ru.itmo.user.accounter.model.entity.UsersAccountsEntity
import ru.itmo.user.accounter.service.PassportService
import ru.itmo.user.accounter.service.UsersAccountsService
import ru.itmo.user.accounter.service.WebSocketService
import ru.itmo.user.accounter.service.toDto
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture
import javax.naming.ServiceUnavailableException
import kotlin.test.assertFailsWith

class PassportServiceTest {
    private val webSocketService = mock(WebSocketService::class.java)
    private val objectMapper = ObjectMapper()
    private val usersAccountsService = mock(UsersAccountsService::class.java)
    private val passportService = PassportService(webSocketService, objectMapper, usersAccountsService)



    @Test
    fun `uploadUserPassport uploads file successfully`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(200)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        ).toDto()

        val fileContent = "sample file content".toByteArray()
        val multipartFile = mock(MultipartFile::class.java)
        val encodedFile = Base64.getEncoder().encodeToString(fileContent)
        val expectedUrl = "http://example.com/passport.pdf"

        `when`(multipartFile.inputStream).thenReturn(fileContent.inputStream())

        `when`(usersAccountsService.getUserAccount(userId))
            .thenReturn(Mono.just(usersAccountsEntity))
        val future = CompletableFuture.completedFuture(expectedUrl)
        `when`(webSocketService.uploadFile(any())).thenReturn(future)

        val result = passportService.uploadUserPassport(multipartFile, userId)

        StepVerifier.create(result)
            .assertNext { url ->
                assertEquals(expectedUrl, url)
            }
            .verifyComplete()

        verify(usersAccountsService).getUserAccount(userId)
        verify(webSocketService).uploadFile(FileDto(userId.toString(), encodedFile))
    }

    @Test
    fun `uploadUserPassport throws ResourceNotFoundException when user is not found`() {
        val userId = 2L
        val multipartFile = mock(MultipartFile::class.java)
        `when`(usersAccountsService.getUserAccount(userId))
            .thenReturn(Mono.empty())

        val result = passportService.uploadUserPassport(multipartFile, userId)

        StepVerifier.create(result)
            .expectErrorMatches { ex ->
                ex is ResourceNotFoundException && ex.message == "User with id $userId not found"
            }
            .verify()

        verify(usersAccountsService).getUserAccount(userId)
        verifyNoInteractions(webSocketService)
    }

    @Test
    fun `getUserPassport returns InputStreamResource when file is downloaded successfully`() {
        val userId = 1L
        val fileContent = "sample file content".toByteArray()
        val encodedFile = Base64.getEncoder().encodeToString(fileContent)
        val stringPayload = """{"fileName":"1","fileBase64":"$encodedFile"}"""

        val future = CompletableFuture.completedFuture(stringPayload)
        `when`(webSocketService.downloadFile(userId.toString())).thenReturn(future)

        val result = passportService.getUserPassport(userId)

        assertEquals(fileContent.decodeToString(), result.inputStream.readAllBytes().decodeToString())
        verify(webSocketService).downloadFile(userId.toString())
    }

    @Test
    fun `getUserPassport throws ServiceUnavailableException when error code is returned`() {
        val userId = 2L
        val stringPayload = "error.code"

        val future = CompletableFuture.completedFuture(stringPayload)
        `when`(webSocketService.downloadFile(userId.toString())).thenReturn(future)

        val exception = assertThrows<ServiceUnavailableException> {
            passportService.getUserPassport(userId)
        }
        assertEquals("Code exec service is unavailable", exception.message)

        verify(webSocketService).downloadFile(userId.toString())
    }

    @Test
    fun `getPassportUrl returns URL successfully`() {
        val userId = 1L
        val expectedUrl = "http://example.com/passport"
        `when`(webSocketService.getUrl(userId.toString()))
            .thenReturn(CompletableFuture.completedFuture(expectedUrl))

        val result = passportService.getPassportUrl(userId)
        assertEquals(expectedUrl, result)

        verify(webSocketService).getUrl(userId.toString())
    }

    @Test
    fun `getPassportUrl throws exception when future fails`() {
        val userId = 2L
        `when`(webSocketService.getUrl(userId.toString()))
            .thenReturn(CompletableFuture.completedFuture("error.code"))

        val result = passportService.getPassportUrl(userId)
        assertEquals("error.code", result)

        verify(webSocketService).getUrl(userId.toString())
    }

    @Test
    fun `deletePassport completes successfully when file is deleted and user is updated`() {
        val userId = 1L
        val registrationDate = Instant.now()
        val email = "niktog1@mail.ru"
        val accountVerified = true
        val phoneNumber = "79991035532"
        val balanceAmount = BigDecimal(200)

        val usersAccountsEntity = UsersAccountsEntity(
            userId = userId,
            balanceAmount = balanceAmount,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        ).toDto()

        val updatedArgs = UpdateUserRequestDto(accountVerified = false)

        `when`(webSocketService.deleteFile(userId.toString()))
            .thenReturn(CompletableFuture.completedFuture("success"))
        `when`(usersAccountsService.getUserAccount(userId))
            .thenReturn(Mono.just(usersAccountsEntity))
        `when`(usersAccountsService.updateUserAccount(updatedArgs, userId))
            .thenReturn(Mono.just(usersAccountsEntity.copy(accountVerified = false)))

        StepVerifier.create(Mono.fromRunnable<Unit> { passportService.deletePassport(userId) })
            .verifyComplete()

        verify(webSocketService).deleteFile(userId.toString())
        verify(usersAccountsService).getUserAccount(userId)
        verify(usersAccountsService).updateUserAccount(updatedArgs, userId)
    }

    @Test
    fun `deletePassport fails when deleteFile returns error code`() {
        val userId = 2L

        `when`(webSocketService.deleteFile(userId.toString()))
            .thenReturn(CompletableFuture.completedFuture("error.code"))

        val exception = assertFailsWith<Exception> {
            passportService.deletePassport(userId)
        }
        assertEquals("javax.naming.ServiceUnavailableException: Code exec service is unavailable", exception.message)

        verify(webSocketService).deleteFile(userId.toString())
        verifyNoInteractions(usersAccountsService)
    }
}