package ru.itmo.bets.handler.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.bets.handler.model.request.CreateBetsGroupsRequestDto
import ru.itmo.bets.handler.model.request.UpdateAvailableBetRequestDto
import ru.itmo.bets.handler.service.AdminBetService
import ru.itmo.bets.handler.service.BetsService
import ru.itmo.bets.handler.service.toResponse
import ru.itmo.common.utils.MAX_PAGE_SIZE
import ru.itmo.vtbet.model.response.AvailableBetsResponse
import ru.itmo.vtbet.model.response.BetGroupResponse
import ru.itmo.vtbet.model.response.FullTypeOfBetMatchResponse

@RestController
@Validated
class AdminBetController(
    private val adminBetService: AdminBetService,
    private val betsService: BetsService,
) {
    @PostMapping("admin/bets/groups")
    @ResponseStatus(HttpStatus.CREATED)
    fun createBetGroup(
        @Valid
        @RequestBody createBetsGroupsRequestDto: CreateBetsGroupsRequestDto,
    ) = betsService.createBetGroup(createBetsGroupsRequestDto).map { it.toResponse() }

    @PutMapping("admin/bets/{id}")
    fun modifyBetGroup(
        @PathVariable("id") betId: Long,
        @Valid
        @RequestBody updateAvailableBetRequestDto: UpdateAvailableBetRequestDto,
    ): AvailableBetsResponse = adminBetService.updateAvailableBet(betId, updateAvailableBetRequestDto).toResponse()

    @PostMapping("admin/matches/{id}/bets")
    fun createTypeOfBetMatch(
        @PathVariable("id") matchId: Long,
        @RequestBody createAvailableBetRequestDto: CreateAvailableBetRequestDto,
    ): FullTypeOfBetMatchResponse =
        adminBetService.createAvailableBet(matchId, createAvailableBetRequestDto).toResponse()

    @PostMapping("admin/matches/{id}/bets/results")
    fun countResultsForMatch(
        @PathVariable(name = "id") matchId: Long,
        @RequestBody successfullAvailableBetsIds: Set<Long>,
    ) = adminBetService.countResultsForMatch(matchId, successfullAvailableBetsIds)

    @PutMapping("admin/bets/{id}/closed")
    fun closeBetsForMatch(
        @PathVariable(name = "id") availableBetId: Long,
    ) = adminBetService.closeBetsForMatch(availableBetId)

    @GetMapping("admin/bets/groups/{id}")
    fun getBetGroup(
        @PathVariable(name = "id") betGroupId: Long,
    ) = adminBetService.getBetGroup(betGroupId).toResponse()

    @GetMapping("admin/bets/{id}")
    fun getAvailableBet(
        @PathVariable(name = "id") availableBetId: Long,
    ) = adminBetService.getAvailableBet(availableBetId).toResponse()

    @GetMapping("admin/bets")
    fun getAvailableBets(
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<AvailableBetsResponse>> {
        val result = adminBetService.getAvailableBets(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map { it.toResponse() },
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("admin/bets/{id}")
    fun deleteAvailableBet(
        @PathVariable(name = "id") availableBetId: Long,
    ) = adminBetService.deleteAvailableBet(availableBetId)

    @GetMapping("admin/bets/groups")
    fun getBetGroups(
        @PositiveOrZero
        @RequestParam("page", defaultValue = "0", required = false) pageNumber: Int,
        @Max(MAX_PAGE_SIZE)
        @Positive
        @RequestParam("size", defaultValue = "50", required = false) pageSize: Int,
    ): ResponseEntity<List<BetGroupResponse>> {
        val result = adminBetService.getBetGroups(pageNumber, pageSize)
        return ResponseEntity(
            result.items.map { it.toResponse() },
            preparePagingHeaders(result.total, result.page, result.pageSize),
            HttpStatus.OK
        )
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("admin/bets/groups/{id}")
    fun deleteBetGroup(
        @PathVariable(name = "id") betGroupId: Long,
    ) = adminBetService.deleteBetGroup(betGroupId)
}
