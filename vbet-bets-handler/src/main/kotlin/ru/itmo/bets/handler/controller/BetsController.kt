package ru.itmo.bets.handler.controller

import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.bets.handler.request.CreateBetsGroupsRequestDto
import ru.itmo.bets.handler.request.UpdateAvailableBetRequestDto
import ru.itmo.bets.handler.service.BetsService
import ru.itmo.bets.handler.service.ComplexBetsService
import ru.itmo.bets.handler.service.toResponse
import ru.itmo.common.response.AvailableBetsResponse
import ru.itmo.common.response.BetGroupResponse
import ru.itmo.common.utils.MAX_PAGE_SIZE

@Validated
@RestController
@RequestMapping("/bets")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Bets_controller", description = "API for bets")
class BetsController(
    private val complexBetsService: ComplexBetsService,
    private val betsService: BetsService,
) {
    @GetMapping
    fun getAvailableBets(
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<AvailableBetsResponse>> {
        val result = complexBetsService.getAvailableBets(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map { it.toResponse() },
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK,
        )
    }

    @GetMapping("/{id}")
    fun getAvailableBet(
        @PathVariable(name = "id") availableBetId: Long,
    ): AvailableBetsResponse = complexBetsService.getAvailableBet(availableBetId).toResponse()

    @PutMapping("/{id}")
    fun modifyAvailableBet(
        @PathVariable("id") betId: Long,
        @Valid
        @RequestBody updateAvailableBetRequestDto: UpdateAvailableBetRequestDto,
    ): AvailableBetsResponse = complexBetsService.updateAvailableBet(betId, updateAvailableBetRequestDto).toResponse()

    @DeleteMapping("/{id}")
    fun deleteAvailableBet(
        @PathVariable(name = "id") availableBetId: Long,
    ) = complexBetsService.deleteAvailableBet(availableBetId)

    @PutMapping("/{id}/closed")
    fun closeBetsForMatch(
        @PathVariable(name = "id") availableBetId: Long,
    ) = complexBetsService.closeBetsForMatch(availableBetId)

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/groups")
    fun getBetGroups(
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<BetGroupResponse>> {
        val result = betsService.getBetGroups(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map { it.toResponse() },
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @GetMapping("/groups/{id}")
    fun getBetGroup(
        @PathVariable(name = "id") betGroupId: Long,
    ) = betsService.getBetGroup(betGroupId)?.toResponse()
        ?: throw ResourceNotFoundException("Group with id: $betGroupId was not found")

    @PostMapping("/groups")
    @ResponseStatus(HttpStatus.CREATED)
    fun createBetGroup(
        @Valid
        @RequestBody createBetsGroupsRequestDto: CreateBetsGroupsRequestDto,
    ) = betsService.createBetGroup(createBetsGroupsRequestDto).map { it.toResponse() }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/groups/{id}")
    fun deleteBetGroup(
        @PathVariable(name = "id") betGroupId: Long,
    ) = betsService.deleteBetGroup(betGroupId)
}
