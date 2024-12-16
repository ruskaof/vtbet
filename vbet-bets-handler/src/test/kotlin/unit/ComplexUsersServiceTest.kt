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
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.test.util.ReflectionTestUtils
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.client.UserAccountClient
import ru.itmo.bets.handler.kafka.KafkaProducer
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.bets.handler.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.bets.handler.model.dto.MatchDto
import ru.itmo.bets.handler.model.dto.SportDto
import ru.itmo.bets.handler.request.MakeBetRequestDto
import ru.itmo.bets.handler.service.*
import ru.itmo.common.dto.BetDto
import ru.itmo.common.dto.BetGroupDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.IllegalBetActionException
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.BalanceActionRequestDto
import ru.itmo.common.request.BalanceActionType
import ru.itmo.common.response.UserResponse
import ru.itmo.common.utils.scaled
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(MockitoExtension::class)
class ComplexUsersServiceTest {

    @Mock
    lateinit var sportsClient: SportsClient

    @Mock
    lateinit var userAccountClient: UserAccountClient

    @Mock
    lateinit var availableBetsService: AvailableBetsService

    @Mock
    lateinit var betsService: BetsService

    @Mock
    lateinit var kafkaProducer: KafkaProducer

    private val ratioDecreaseValue: BigDecimal = BigDecimal("0.5")

    @InjectMocks
    lateinit var complexUsersService: ComplexUsersService

    private lateinit var makeBetRequestDto: MakeBetRequestDto
    private lateinit var userDto: UserResponse
    private lateinit var availableBetWithGroup: AvailableBetWithBetGroupDto
    private lateinit var availableBet: AvailableBetDto
    private lateinit var matchDto: MatchDto
    private lateinit var betDto: BetDto

    private val userId = 1L
    private val availableBetId = 2L
    private val betAmount = BigDecimal("100")
    private val ratio = BigDecimal("2.0")

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(complexUsersService, "ratioDecreaseValue", ratioDecreaseValue)
        // Prepare mock data
        makeBetRequestDto = MakeBetRequestDto(
            availableBetId = availableBetId,
            amount = betAmount,
            ratio = ratio
        )

        userDto = UserResponse(
            id = userId,
            accountVerified = true,
            email = "test@test.com",
            phoneNumber = "89998222822",
            balanceAmount = BigDecimal("100"),
            registrationDate = Instant.now(),
        )

        availableBet = AvailableBetDto(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = false,
            matchId = 10L,
            groupId = 2L,
        )

        availableBetWithGroup = AvailableBetWithBetGroupDto(
            availableBetId = availableBetId,
            ratio = ratio,
            betsClosed = false,
            matchId = 10L,
            betGroupDto = BetGroupDto(groupId = 1L, description = "Group A")
        )

        matchDto = MatchDto(matchId = 10L, name = "Match A", sport = SportDto(sportId = 1L, name = "Sport A"), ended = false)

        betDto = BetDto(
            betId = 100L,
            amount = betAmount,
            ratio = ratio,
            userId = userId,
            availableBetId = availableBetId
        )
    }

    @Test
    fun `should create bet successfully`() {
        // Arrange
        whenever(userAccountClient.getUser(userId)).thenReturn(userDto)
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(availableBetWithGroup)
        whenever(sportsClient.getMatch(availableBetWithGroup.matchId)).thenReturn(matchDto.toResponse())
        whenever(betsService.createBet(userId, availableBetWithGroup.toAvailableBetDto(), ratio.scaled(), betAmount)).thenReturn(betDto)
        whenever(userAccountClient.updateBalance(userId, BalanceActionRequestDto(
            BalanceActionType.WITHDRAW, betAmount
        )
        )).thenReturn(userDto)
        whenever(availableBetsService.update(any())).thenReturn(availableBet)

        // Act
        val result = complexUsersService.makeBet(userId, makeBetRequestDto)

        // Assert
        assertNotNull(result)
        assertEquals(betDto.betId, result.betId)
        verify(userAccountClient).getUser(userId)
        verify(availableBetsService).getAvailableBetWithGroup(availableBetId)
        verify(sportsClient).getMatch(availableBetWithGroup.matchId)
        verify(betsService).createBet(userId, availableBetWithGroup.toAvailableBetDto(), ratio.scaled(), betAmount)
        verify(userAccountClient).updateBalance(userId, BalanceActionRequestDto(BalanceActionType.WITHDRAW, betAmount))
    }

    @Test
    fun `should throw ResourceNotFoundException when user not found`() {
        // Arrange
        whenever(userAccountClient.getUser(userId)).thenThrow(FeignException.NotFound("", Request.create(Request.HttpMethod.GET, "", emptyMap(), Request.Body.empty(), RequestTemplate()), null, null))

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexUsersService.makeBet(userId, makeBetRequestDto)
        }

        assertEquals("User account not found", exception.message)
        verify(userAccountClient).getUser(userId)
    }

    @Test
    fun `should throw ResourceNotFoundException when available bet not found`() {
        // Arrange
        whenever(userAccountClient.getUser(userId)).thenReturn(userDto)
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexUsersService.makeBet(userId, makeBetRequestDto)
        }

        assertEquals("No available bet found with ID: $availableBetId", exception.message)
    }

    @Test
    fun `should throw IllegalBetActionException when bet is closed`() {
        // Arrange
        val closedBet = availableBetWithGroup.copy(betsClosed = true)
        whenever(userAccountClient.getUser(userId)).thenReturn(userDto)
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(closedBet)

        // Act & Assert
        val exception = assertThrows<IllegalBetActionException> {
            complexUsersService.makeBet(userId, makeBetRequestDto)
        }

        assertEquals("Bets on available bet with ID: $availableBetId are closed", exception.message)
    }

    @Test
    fun `should throw ResourceNotFoundException when match not found`() {
        // Arrange
        whenever(userAccountClient.getUser(userId)).thenReturn(userDto)
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(availableBetWithGroup)
        whenever(sportsClient.getMatch(availableBetWithGroup.matchId)).thenThrow(FeignException.NotFound("", Request.create(Request.HttpMethod.GET, "", emptyMap(), Request.Body.empty(), RequestTemplate()), null, null))

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            complexUsersService.makeBet(userId, makeBetRequestDto)
        }

        assertEquals("Match not found", exception.message)
    }

    @Test
    fun `should throw IllegalBetActionException when match is already finished`() {
        // Arrange
        val finishedMatch = matchDto.copy(ended = true)
        whenever(userAccountClient.getUser(userId)).thenReturn(userDto)
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(availableBetWithGroup)
        whenever(sportsClient.getMatch(availableBetWithGroup.matchId)).thenReturn(finishedMatch.toResponse())

        // Act & Assert
        val exception = assertThrows<IllegalBetActionException> {
            complexUsersService.makeBet(userId, makeBetRequestDto)
        }

        assertEquals("Match has been already finished", exception.message)
    }

    @Test
    fun `should throw IllegalBetActionException when ratio is incorrect`() {
        // Arrange
        val wrongRatioRequest = makeBetRequestDto.copy(ratio = BigDecimal("1.5"))
        whenever(sportsClient.getMatch(availableBet.matchId)).thenReturn(matchDto.toResponse())
        whenever(userAccountClient.getUser(userId)).thenReturn(userDto)
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(availableBetWithGroup)

        // Act & Assert
        val exception = assertThrows<IllegalBetActionException> {
            complexUsersService.makeBet(userId, wrongRatioRequest)
        }

        assertEquals("Wrong ratio: ratio now is ${availableBetWithGroup.ratio}", exception.message)
    }

    @Test
    fun `should throw IllegalBetActionException when insufficient balance`() {
        // Arrange
        whenever(userAccountClient.getUser(userId)).thenReturn(userDto)
        whenever(availableBetsService.getAvailableBetWithGroup(availableBetId)).thenReturn(availableBetWithGroup)
        whenever(sportsClient.getMatch(availableBetWithGroup.matchId)).thenReturn(matchDto.toResponse())
        whenever(betsService.createBet(userId, availableBetWithGroup.toAvailableBetDto(), ratio.scaled(), betAmount)).thenReturn(betDto)
        whenever(userAccountClient.updateBalance(userId, BalanceActionRequestDto(
            BalanceActionType.WITHDRAW, betAmount
        ))).thenThrow(FeignException.BadRequest("", Request.create(Request.HttpMethod.GET, "", emptyMap(), Request.Body.empty(), RequestTemplate()), null, null))

        // Act & Assert
        val exception = assertThrows<IllegalBetActionException> {
            complexUsersService.makeBet(userId, makeBetRequestDto)
        }

        assertEquals("Could not withdraw money from user. Account might have not enough money", exception.message)
    }

    @Test
    fun `should return user bets successfully`() {
        // Arrange
        val pagingDto = PagingDto<BetDto>(listOf(betDto), 1, 1, 1)
        whenever(betsService.getUserBets(userId, 0, 10)).thenReturn(pagingDto)

        // Act
        val result = complexUsersService.getUserBets(userId, 0, 10)

        // Assert
        assertNotNull(result)
        assertEquals(1, result.pageSize)
        assertEquals(betDto.betId, result.items[0].betId)
    }
}
