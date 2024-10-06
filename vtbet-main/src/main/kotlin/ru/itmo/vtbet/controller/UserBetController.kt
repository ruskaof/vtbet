package ru.itmo.vtbet.controller;

import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import ru.itmo.vtbet.service.UserBetService

@RestController
@Validated
class UserBetController(
    private val userBetService: UserBetService,
) {

//    @GetMapping("bets/available")
//    fun getAvailableBets(
//        @RequestParam pageNumber: Int,
//        @Max(MAX_PAGE_SIZE)
//        @RequestParam pageSize: Int,
//    ): ResponseEntity<List<AvailableBetResponse>> {
//        val result = userBetService.getAvailableBets(pageNumber, pageSize)
//        return ResponseEntity(
//            result.items.map(AvailableBetDto::toResponse),
//            preparePagingHeaders(result.total, result.page, result.pageSize),
//            HttpStatus.OK
//        )
//    }
}
