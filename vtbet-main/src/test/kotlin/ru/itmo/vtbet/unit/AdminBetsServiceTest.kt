package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.*
import ru.itmo.vtbet.model.dto.*
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.vtbet.model.request.UpdateAvailableBetRequestDto
import ru.itmo.vtbet.service.*
import java.math.BigDecimal
import java.time.Instant

class AdminBetsServiceTest {
    private val matchesService: MatchesService = mock()
    private val betsService: BetsService = mock()
    private val availableBetsService: AvailableBetsService = mock()
    private val complexUsersService: ComplexUsersService = mock()
    private val adminBetService = AdminBetService(
        matchesService = matchesService,
        betsService = betsService,
        availableBetsService = availableBetsService,
        complexUsersService = complexUsersService
    )

    private fun <T> any(type: Class<T>): T = Mockito.any(type)

    @Test
    fun createAvailableBet() {
        `when`(betsService.getBetGroup(1)).thenReturn(BetGroupDto(1, "test"))
        `when`(matchesService.getMatch(1)).thenReturn(MatchDto(1, "test", SportDto(1, "test"), false))
        `when`(availableBetsService.save(this.any(AvailableBetWithBetGroupDto::class.java))).thenReturn(
            AvailableBetWithBetGroupDto(1, BigDecimal("3.33"), BetGroupDto(1, "test"), false, 1)
        )

        val result = adminBetService.createAvailableBet(1, CreateAvailableBetRequestDto(1, BigDecimal(3.333)))
        verify(availableBetsService).save(
            AvailableBetWithBetGroupDto(
                0,
                BigDecimal("3.33"),
                BetGroupDto(1, "test"),
                false,
                1
            )
        )
        Assertions.assertEquals(
            FullAvailableBetWithBetGroupDto(
                1,
                BigDecimal("3.33"),
                false,
                BetGroupDto(1, "test"),
                MatchDto(1, "test", SportDto(1, "test"), false),
            ), result
        )
    }

    @Test
    fun updateAvailableBet() {
        `when`(availableBetsService.getAvailableBetWithGroup(1)).thenReturn(
            AvailableBetWithBetGroupDto(
                1,
                BigDecimal("3.33"),
                BetGroupDto(1, "test"),
                false,
                1
            )
        )

        adminBetService.updateAvailableBet(1, UpdateAvailableBetRequestDto(BigDecimal("1.555")))

        verify(availableBetsService).update(
            AvailableBetWithBetGroupDto(
                1,
                BigDecimal("1.55"),
                BetGroupDto(1, "test"),
                false,
                1
            )
        )
    }

    @Test
    fun countResultsForMatch() {
        `when`(matchesService.getMatch(1)).thenReturn(MatchDto(1, "test", SportDto(1, "test"), false))

        `when`(availableBetsService.getAllByMatchId(1)).thenReturn(
            listOf(
                AvailableBetDto(
                    1,
                    BigDecimal("1.5"),
                    1,
                    false,
                    1
                )
            )
        )
        `when`(betsService.getBetsByAvailableBetIds(listOf(1))).thenReturn(
            listOf(
                BetDto(
                    1,
                    BigDecimal("1.5"),
                    BigDecimal("300"),
                    1,
                    1
                )
            )
        )

        adminBetService.countResultsForMatch(1, setOf(1))

        verify(complexUsersService).addMoneyToUser(1, BigDecimal("450.0"))
        verify(matchesService).endMatch(1)
    }

    @Test
    fun getBetGroups() {
        `when`(betsService.getBetGroups(1, 1)).thenReturn(PagingDto(listOf(BetGroupDto(1, "test")), 1, 0, 1))
        val result = adminBetService.getBetGroups(1, 1)

        Assertions.assertEquals(PagingDto(listOf(BetGroupDto(1, "test")), 1, 0, 1), result)
    }

    @Test
    fun getAvailableBets() {
        `when`(availableBetsService.getAvailableBets(1, 1)).thenReturn(PagingDto(listOf(AvailableBetDto(1, BigDecimal("1.5"), 1, false, 1)), 1, 0, 1))
        val result = adminBetService.getAvailableBets(1, 1)

        Assertions.assertEquals(PagingDto(listOf(AvailableBetDto(1, BigDecimal("1.5"), 1, false, 1)), 1, 0, 1), result)
    }

    @Test
    fun getAvailableBet() {
        `when`(availableBetsService.getAvailableBet(1)).thenReturn(AvailableBetDto(1, BigDecimal("1.5"), 1, false, 1))
        val result = adminBetService.getAvailableBet(1)

        Assertions.assertEquals(AvailableBetDto(1, BigDecimal("1.5"), 1, false, 1), result)
    }

    @Test
    fun getBetGroup() {
        `when`(betsService.getBetGroup(1)).thenReturn(BetGroupDto(1, "test"))
        val result = adminBetService.getBetGroup(1)

        Assertions.assertEquals(BetGroupDto(1, "test"), result)
    }

    @Test
    fun closeBetsForMatch() {
        `when`(availableBetsService.getAvailableBetWithGroup(1)).thenReturn(AvailableBetWithBetGroupDto(1, BigDecimal("1.5"), BetGroupDto(1, "test"), false, 1))
        adminBetService.closeBetsForMatch(1)

        verify(availableBetsService).update(AvailableBetWithBetGroupDto(1, BigDecimal("1.5"), BetGroupDto(1, "test"), true, 1))
    }
}
