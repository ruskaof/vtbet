package unit

import feign.FeignException
import feign.Request
import feign.RequestTemplate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.bets.handler.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.bets.handler.model.entity.AvailableBetsEntity
import ru.itmo.bets.handler.model.entity.BetsGroupsEntity
import ru.itmo.bets.handler.repository.AvailableBetsRepository
import ru.itmo.bets.handler.service.AvailableBetsService
import ru.itmo.common.dto.BetGroupDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.response.MatchResponse
import ru.itmo.common.response.SportResponse
import java.util.*

@ExtendWith(MockitoExtension::class)
class AvailableBetServiceMockTest {

    @Mock
    lateinit var availableBetsRepository: AvailableBetsRepository

    @Mock
    lateinit var sportsClient: SportsClient

    @InjectMocks
    lateinit var availableBetService: AvailableBetsService

    @Test
    fun `should return paginated list of available bets`() {
        val bet = AvailableBetDto(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            groupId = 1L,
            matchId = 1L,
        )

        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )
        // Arrange
        val pageable = PageRequest.of(0, 2, Sort.by("availableBetId"))
        val availableBetsPage = PageImpl(listOf(entity, entity)) //, 2, 1, 2) // mock page with 2 items
        `when`(availableBetsRepository.findAll(pageable)).thenReturn(availableBetsPage as Page<AvailableBetsEntity>)

        // Act
        val result = availableBetService.getAvailableBets(pageNumber = 0, pageSize = 2)

        // Assert
        assertThat(result.items).hasSize(2)
        assertThat(result.total).isEqualTo(2)
        assertThat(result.pageSize).isEqualTo(2)
        assertThat(result.page).isEqualTo(0)
        verify(availableBetsRepository, times(1)).findAll(pageable)
    }

    @Test
    fun `should return available bet by ID`() {
        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )

        val bet = AvailableBetDto(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            groupId = 1L,
            matchId = 1L,
        )
        // Arrange
        `when`(availableBetsRepository.findById(entity.availableBetId!!)).thenReturn(Optional.of(entity))

        // Act
        val result = availableBetService.getAvailableBet(entity.availableBetId!!)

        // Assert
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(bet)
        assertThat(result?.availableBetId).isEqualTo(entity.availableBetId)
        verify(availableBetsRepository, times(1)).findById(entity.availableBetId!!)
    }

    @Test
    fun `should return null if available bet by ID is not found`() {
        // Arrange
        `when`(availableBetsRepository.findById(999L)).thenReturn(Optional.empty())

        // Act
        val result = availableBetService.getAvailableBet(999L)

        // Assert
        assertThat(result).isNull()
        verify(availableBetsRepository, times(1)).findById(999L)
    }

    @Test
    fun `should return available bet with group by ID`() {
        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )

        val bet = AvailableBetWithBetGroupDto(
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
        `when`(availableBetsRepository.findById(entity.availableBetId!!)).thenReturn(Optional.of(entity))

        // Act
        val result = availableBetService.getAvailableBetWithGroup(entity.availableBetId!!)

        // Assert
        assertThat(result).isNotNull
        assertThat(result).isEqualTo(bet)
        assertThat(result?.availableBetId).isEqualTo(entity.availableBetId)
        verify(availableBetsRepository, times(1)).findById(entity.availableBetId!!)
    }

    @Test
    fun `should return null if available bet with group by ID is not found`() {
        // Arrange
        `when`(availableBetsRepository.findById(999L)).thenReturn(Optional.empty())

        // Act
        val result = availableBetService.getAvailableBetWithGroup(999L)

        // Assert
        assertThat(result).isNull()
        verify(availableBetsRepository, times(1)).findById(999L)
    }

    @Test
    fun `should delete available bet by ID`() {
        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )
        // Act
        availableBetService.delete(entity.availableBetId!!)

        // Assert
        verify(availableBetsRepository, times(1)).deleteById(entity.availableBetId!!)
    }

    @Test
    fun `should update bet and return updated DTO`() {
        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )

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
        `when`(availableBetsRepository.saveAndFlush(any())).thenReturn(entity)

        // Act
        val result = availableBetService.update(testBetDto)

        // Assert
        assertThat(result.availableBetId).isEqualTo(testBetDto.availableBetId)
        verify(availableBetsRepository, times(1)).saveAndFlush(any())
    }

    @Test
    fun `should save new bet with null ID and return DTO with group`() {
        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )

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
        `when`(availableBetsRepository.saveAndFlush(any())).thenReturn(entity)

        // Act
        val result = availableBetService.save(testBetDto)

        // Assert
        assertThat(result.availableBetId).isEqualTo(entity.availableBetId)
        verify(availableBetsRepository, times(1)).saveAndFlush(any())
    }

    @Test
    fun `should return list of bets by match ID`() {
        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )

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
        val matchId = 10L
        val availableBetsList = listOf(entity, entity)
        `when`(availableBetsRepository.findAllByMatchId(matchId)).thenReturn(availableBetsList)

        // Act
        val result = availableBetService.getAllByMatchId(matchId)

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].availableBetId).isEqualTo(entity.availableBetId)
        verify(availableBetsRepository, times(1)).findAllByMatchId(matchId)
    }

    @Test
    fun `should return paginated list of bets by match ID`() {
        val entity = AvailableBetsEntity(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            betsGroupsEntity = BetsGroupsEntity(
                groupId = 1L,
                description = "test",
            ),
            matchId = 1L,
        )

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
        val matchId = 10L
        val pageable = PageRequest.of(0, 2, Sort.by("availableBetId"))
        val availableBetsPage = PageImpl(listOf(entity, entity))
        `when`(availableBetsRepository.findAllByMatchId(matchId, pageable)).thenReturn(availableBetsPage)
        `when`(sportsClient.getMatch(matchId)).thenReturn(MatchResponse(
            id = 1L,
            name = "match",
            sport = SportResponse(
                id = 1L,
                name = "sport",
            ),
            ended = true,
        ))

        // Act
        val result = availableBetService.getAllByMatchId(matchId, pageNumber = 0, pageSize = 2)

        // Assert
        assertThat(result.items).hasSize(2)
        assertThat(result.total).isEqualTo(2)
        assertThat(result.pageSize).isEqualTo(2)
        assertThat(result.page).isEqualTo(0)
        verify(availableBetsRepository, times(1)).findAllByMatchId(matchId, pageable)
        verify(sportsClient, times(1)).getMatch(matchId)
    }

    @Test
    fun `should throw ResourceNotFoundException when sportsClient returns 404`() {
        // Arrange
        val matchId = 10L
        val pageable = PageRequest.of(0, 2, Sort.by("availableBetId"))
        `when`(sportsClient.getMatch(matchId)).thenThrow(FeignException.NotFound("", Request.create(Request.HttpMethod.GET, "", emptyMap(), Request.Body.empty(), RequestTemplate()), null, null))

        // Act & Assert
        assertThrows<ResourceNotFoundException> {
            availableBetService.getAllByMatchId(matchId, pageNumber = 0, pageSize = 2)
        }

        verify(sportsClient, times(1)).getMatch(matchId)
        verify(availableBetsRepository, times(0)).findAllByMatchId(matchId, pageable)
    }

    @Test
    fun `should throw original exception if sportsClient throws non-404 error`() {
        // Arrange
        val matchId = 10L
        val pageable = PageRequest.of(0, 2, Sort.by("availableBetId"))
        val feignException = FeignException.InternalServerError("", Request.create(Request.HttpMethod.GET, "", emptyMap(), Request.Body.empty(), RequestTemplate()), null, null)
        `when`(sportsClient.getMatch(matchId)).thenThrow(feignException)

        // Act & Assert
        val thrownException = assertThrows<FeignException> {
            availableBetService.getAllByMatchId(matchId, pageNumber = 0, pageSize = 2)
        }

        assertThat(thrownException).isEqualTo(feignException)
        verify(sportsClient, times(1)).getMatch(matchId)
        verify(availableBetsRepository, times(0)).findAllByMatchId(matchId, pageable)
    }
}
