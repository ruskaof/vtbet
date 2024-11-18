package unit

import feign.FeignException
import feign.Request
import feign.RequestTemplate
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.times
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.client.UserAccountClient
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.bets.handler.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.bets.handler.model.dto.MatchDto
import ru.itmo.bets.handler.model.dto.SportDto
import ru.itmo.bets.handler.repository.AvailableBetsRepository
import ru.itmo.bets.handler.request.CreateAvailableBetRequestDto
import ru.itmo.bets.handler.request.UpdateAvailableBetRequestDto
import ru.itmo.bets.handler.service.AvailableBetsService
import ru.itmo.bets.handler.service.BetsService
import ru.itmo.bets.handler.service.ComplexBetsService
import ru.itmo.bets.handler.service.toResponse
import ru.itmo.common.dto.BetDto
import ru.itmo.common.dto.BetGroupDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.response.UserResponse
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class ComplexBetsServiceTest {

    @Mock
    lateinit var sportsClient: SportsClient

    @Mock
    lateinit var userAccountClient: UserAccountClient

    @Mock
    lateinit var betsService: BetsService

    @Mock
    lateinit var availableBetsRepository: AvailableBetsRepository

    @Mock
    lateinit var availableBetsService: AvailableBetsService

    @InjectMocks
    lateinit var complexBetsService: ComplexBetsService

    private lateinit var availableBetDto: AvailableBetDto
    private lateinit var updateAvailableBetRequestDto: UpdateAvailableBetRequestDto
    private lateinit var updatedBetDto: AvailableBetDto
    private val availableBetId = 100L

    private lateinit var createAvailableBetRequestDto: CreateAvailableBetRequestDto
    private lateinit var group: BetGroupDto
    private lateinit var match: MatchDto
    private lateinit var availableBet: AvailableBetWithBetGroupDto
    private lateinit var successfulBet: BetDto
    private val matchId = 123L
    private val groupId = 456L
    private val betId = 789L

    @BeforeEach
    fun setup() {
        availableBetDto = AvailableBetDto(
            availableBetId = availableBetId,
            ratio = BigDecimal("2.0"),
            groupId = 1L,
            betsClosed = false,
            matchId = 1L
        )

        updateAvailableBetRequestDto = UpdateAvailableBetRequestDto(
            ratio = BigDecimal("2.5")
        )

        updatedBetDto = availableBetDto.copy(ratio = updateAvailableBetRequestDto.ratio)

        createAvailableBetRequestDto = CreateAvailableBetRequestDto(
            groupId = groupId,
            ratio = BigDecimal("2.0")
        )

        group = BetGroupDto(groupId = groupId, description = "Group A")
        match = MatchDto(matchId = matchId, ended = false, name = "Match A", sport = SportDto(sportId = 1L, name = "Sport A"))
        availableBet = AvailableBetWithBetGroupDto(
            availableBetId = betId,
            ratio = BigDecimal("2.0"),
            betsClosed = false,
            matchId = matchId,
            betGroupDto = group
        )
        successfulBet = BetDto(
            betId = betId,
            amount = BigDecimal("100.0"),
            ratio = BigDecimal("2.0"),
            userId = 1L,
            availableBetId = betId
        )
    }

    @Test
    fun `should return available bets`() {
        // Arrange
        val pageNumber = 0
        val pageSize = 10
        val pagingDto = PagingDto(
            items = listOf(availableBetDto),
            total = 1,
            pageSize = pageSize,
            page = pageNumber
        )
        whenever(availableBetsService.getAvailableBets(pageNumber, pageSize)).thenReturn(pagingDto)

        // Act
        val result = complexBetsService.getAvailableBets(pageNumber, pageSize)

        // Assert
        assertNotNull(result)
        assertEquals(1, result.items.size)
        verify(availableBetsService).getAvailableBets(pageNumber, pageSize)
    }

    @Test
    fun `should return available bet by id`() {
        // Arrange
        whenever(availableBetsService.getAvailableBet(availableBetId)).thenReturn(availableBetDto)

        // Act
        val result = complexBetsService.getAvailableBet(availableBetId)

        // Assert
        assertNotNull(result)
        assertEquals(availableBetDto, result)
        verify(availableBetsService).getAvailableBet(availableBetId)
    }

    @Test
    fun `should throw ResourceNotFoundException when available bet not found by id`() {
        // Arrange
        whenever(availableBetsService.getAvailableBet(availableBetId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexBetsService.getAvailableBet(availableBetId)
        }

        assertEquals("Available bet with id $availableBetId not found", exception.message)
        verify(availableBetsService).getAvailableBet(availableBetId)
    }

    @Test
    fun `should update available bet`() {
        val testBetDto = AvailableBetWithBetGroupDto(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betGroupDto = BetGroupDto(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )
        // Arrange
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(testBetDto)
        whenever(availableBetsService.update(any())).thenReturn(updatedBetDto)

        // Act
        val result = complexBetsService.updateAvailableBet(availableBetId, updateAvailableBetRequestDto)

        // Assert
        assertNotNull(result)
        assertEquals(updatedBetDto.ratio, result.ratio)
        verify(availableBetsService).getAvailableBetWithGroup(availableBetId)
        verify(availableBetsService).update(any())
    }

    @Test
    fun `should throw ResourceNotFoundException when updating bet not found`() {
        // Arrange
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexBetsService.updateAvailableBet(availableBetId, updateAvailableBetRequestDto)
        }

        assertEquals("Bet not found", exception.message)
        verify(availableBetsService).getAvailableBetWithGroup(availableBetId)
    }

    @Test
    fun `should delete available bet`() {
        // Arrange
        whenever(availableBetsService.getAvailableBet(availableBetId)).thenReturn(availableBetDto)

        // Act
        complexBetsService.deleteAvailableBet(availableBetId)

        // Assert
        verify(availableBetsService).getAvailableBet(availableBetId)
        verify(availableBetsService).delete(availableBetId)
    }

    @Test
    fun `should throw ResourceNotFoundException when deleting bet not found`() {
        // Arrange
        whenever(availableBetsService.getAvailableBet(availableBetId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexBetsService.deleteAvailableBet(availableBetId)
        }

        assertEquals("Available bet with id $availableBetId not found", exception.message)
        verify(availableBetsService).getAvailableBet(availableBetId)
        verify(availableBetsService, times(0)).delete(availableBetId)
    }

    @Test
    fun `should close bets for match`() {
        val testBetDto = AvailableBetWithBetGroupDto(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betGroupDto = BetGroupDto(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )
        // Arrange
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(testBetDto)
        whenever(availableBetsService.update(any())).thenReturn(availableBetDto.copy(betsClosed = true))

        // Act
        complexBetsService.closeBetsForMatch(availableBetId)

        // Assert
        verify(availableBetsService).getAvailableBetWithGroup(availableBetId)
        verify(availableBetsService).update(any())
    }

    @Test
    fun `should throw ResourceNotFoundException when closing bet for match not found`() {
        // Arrange
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexBetsService.closeBetsForMatch(availableBetId)
        }

        assertEquals("Available  with id $availableBetId not found", exception.message)
        verify(availableBetsService).getAvailableBetWithGroup(availableBetId)
        verify(availableBetsService, times(0)).update(any())
    }

    @Test
    fun `should create available bet successfully`() {
        // Arrange
        whenever(betsService.getBetGroup(groupId)).thenReturn(group)
        whenever(sportsClient.getMatch(matchId)).thenReturn(match.toResponse())
        whenever(availableBetsService.save(any())).thenReturn(availableBet)

        // Act
        val result = complexBetsService.createAvailableBet(matchId, createAvailableBetRequestDto)

        // Assert
        assertNotNull(result)
        assertEquals(betId, result.availableBetId)
        assertEquals("2.00", result.ratio.toPlainString())
        verify(betsService).getBetGroup(groupId)
        verify(sportsClient).getMatch(matchId)
        verify(availableBetsService).save(any())
    }

    @Test
    fun `should throw ResourceNotFoundException if bet group not found`() {
        // Arrange
        whenever(betsService.getBetGroup(groupId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexBetsService.createAvailableBet(matchId, createAvailableBetRequestDto)
        }

        assertEquals("Bet group not found", exception.message)
        verify(betsService).getBetGroup(groupId)
        verify(sportsClient, times(0)).getMatch(matchId)
    }

    @Test
    fun `should throw ResourceNotFoundException if match not found`() {
        // Arrange
        whenever(betsService.getBetGroup(groupId)).thenReturn(group)
        whenever(sportsClient.getMatch(matchId)).thenThrow(FeignException.NotFound("", Request.create(Request.HttpMethod.GET, "", emptyMap(), Request.Body.empty(), RequestTemplate()), null, null))

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexBetsService.createAvailableBet(matchId, createAvailableBetRequestDto)
        }

        assertEquals("Match not found", exception.message)
        verify(betsService).getBetGroup(groupId)
        verify(sportsClient).getMatch(matchId)
    }

    @Test
    fun `should throw IllegalBetActionException when match has ended`() {
        // Arrange
        val endedMatch = match.copy(ended = true)
        whenever(sportsClient.getMatch(matchId)).thenReturn(endedMatch.toResponse())

        // Act & Assert
        val exception = assertThrows<IllegalBetActionException> {
            complexBetsService.countResultsForMatch(matchId, setOf(betId))
        }

        assertEquals("Match with id $matchId is already ended", exception.message)
        verify(sportsClient).getMatch(matchId)
        verify(availableBetsService, times(0)).getAllByMatchId(any())
    }

    @Test
    fun `should process successful bets when match is active`() {
        val availableBetDto = AvailableBetDto(
            availableBetId = betId,
            ratio = BigDecimal("2.0"),
            betsClosed = false,
            matchId = matchId,
            groupId = groupId,
        )

        val user = UserResponse(
            id = 1L,
            email = "test@test.com",
            phoneNumber = "+7123456789",
            accountVerified = true,
            registrationDate = Instant.now(),
            balanceAmount = BigDecimal("1.0"),
        )
        // Arrange
        whenever(sportsClient.getMatch(matchId)).thenReturn(match.toResponse())
        whenever(availableBetsService.getAllByMatchId(matchId)).thenReturn(listOf(availableBetDto))
        whenever(betsService.getBetsByAvailableBetIds(listOf(betId))).thenReturn(listOf(successfulBet))
        whenever(userAccountClient.updateBalance(any(), any())).thenReturn(user)

        // Act
        complexBetsService.countResultsForMatch(matchId, setOf(betId))

        // Assert
        verify(userAccountClient).updateBalance(successfulBet.userId, BalanceActionRequestDto(
            BalanceActionType.DEPOSIT,
            successfulBet.amount * successfulBet.ratio
        )
        )
        verify(sportsClient).endMatch(matchId, true)
    }

    @Test
    fun `should not process bets if no successful bets`() {
        val availableBetDto = AvailableBetDto(
            availableBetId = betId,
            ratio = BigDecimal("2.0"),
            betsClosed = false,
            matchId = matchId,
            groupId = groupId,
        )
        // Arrange
        val noSuccessfulBets = emptySet<Long>()
        whenever(sportsClient.getMatch(matchId)).thenReturn(match.toResponse())
        whenever(availableBetsService.getAllByMatchId(matchId)).thenReturn(listOf(availableBetDto))
        whenever(betsService.getBetsByAvailableBetIds(listOf(betId))).thenReturn(listOf(successfulBet))

        // Act
        complexBetsService.countResultsForMatch(matchId, noSuccessfulBets)

        // Assert
        verify(userAccountClient, times(0)).updateBalance(any(), any())
        verify(sportsClient).endMatch(matchId, true)
    }
}
