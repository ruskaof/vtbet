package ru.itmo.user.accounter.controller

import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import ru.itmo.user.accounter.service.PassportService

@RestController
class PassportController(
    private val passportService: PassportService,
) {
    @PostMapping("/users/{id}/passport")
    fun uploadUserPassport(
        @PathVariable("id") userId: Long,
        @RequestParam("file") file: MultipartFile,
    ) = passportService.uploadUserPassport(file, userId)

    @GetMapping("/users/{id}/passport/file")
    fun getUserPassport(
        @PathVariable("id") userId: Long,
    ): ResponseEntity<*> {
        val inputStream = passportService.getUserPassport(userId)
        val contentType = "application/octet-stream"
        val headerValue = "attachment; filename=\"$userId\""

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
            .body<InputStreamResource>(inputStream)
    }

    @GetMapping("/users/{id}/passport/url")
    fun getUserPassportUrl(@PathVariable("id") userId: Long) = passportService.getPassportUrl(userId)

    @DeleteMapping("/users/{id}/passport")
    @ResponseStatus(HttpStatus.I_AM_A_TEAPOT)
    fun deletePassport(@PathVariable("id") userId: Long) = passportService.deletePassport(userId)
}
