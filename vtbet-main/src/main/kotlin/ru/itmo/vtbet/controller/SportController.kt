package ru.itmo.vtbet.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.dto.toResponse
import ru.itmo.vtbet.model.request.CreateSportRequest
import ru.itmo.vtbet.model.response.SportResponse
import ru.itmo.vtbet.service.SportService

@RestController
@Validated
class SportController(
    private val sportService: SportService,
) {

    @GetMapping("/sport")
    fun getSports(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): ResponseEntity<List<SportResponse>> {
        val result = sportService.getSports(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map(SportDto::toResponse),
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @PostMapping("/sport")
    fun createSport(
        @RequestBody createSportRequest: CreateSportRequest,
    ): SportResponse =
        sportService.createSport(createSportRequest).toResponse()
}