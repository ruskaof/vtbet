package ru.itmo.user.accounter.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.InputStreamResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ru.itmo.common.dto.FileDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.UpdateUserRequestDto
import java.util.*
import javax.naming.ServiceUnavailableException

@Service
class PassportService(
    private val webSocketService: WebSocketService,
    private val objectMapper: ObjectMapper,
    private val usersAccountsService: UsersAccountsService,
) {
    fun uploadUserPassport(file: MultipartFile, userId: Long): Mono<String> =
        usersAccountsService.getUserAccount(userId)
            .switchIfEmpty(Mono.error(ResourceNotFoundException("User with id $userId not found")))
            .publishOn(Schedulers.boundedElastic())
            .map {
                val fileDto = FileDto(
                    userId.toString(),
                    Base64.getEncoder().encodeToString(file.inputStream.readAllBytes())
                )
                val future = webSocketService.uploadFile(fileDto)
                future.get()
            }

    fun getUserPassport(userId: Long): InputStreamResource {
        val future = webSocketService.downloadFile(userId.toString())
        val stringPayload = future.get()
        if (stringPayload.equals("error.code")) {
            throw ServiceUnavailableException("Code exec service is unavailable")
        }
        val fileDto = objectMapper.readValue(stringPayload, FileDto::class.java)
        return InputStreamResource(Base64.getDecoder().decode(fileDto.fileBase64).inputStream())
    }

    fun getPassportUrl(userId: Long): String {
        val future = webSocketService.getUrl(userId.toString())
        return future.get()
    }

    fun deletePassport(userId: Long) {
        Mono.defer {
            Mono.fromFuture(webSocketService.deleteFile(userId.toString()))
        }.flatMap { result ->
            if (result == "success") {
                usersAccountsService.getUserAccount(userId)
            } else {
                Mono.error(ServiceUnavailableException("Code exec service is unavailable"))
            }
        }.switchIfEmpty(Mono.error(ResourceNotFoundException("User with id $userId not found")))
            .flatMap {
                usersAccountsService.updateUserAccount(UpdateUserRequestDto(accountVerified = false), userId)
            }.block()
    }
}