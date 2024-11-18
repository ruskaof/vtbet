package unit

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.bets.handler.model.entity.BetsEntity
import ru.itmo.bets.handler.model.entity.BetsGroupsEntity
import ru.itmo.bets.handler.repository.BetsGroupsRepository
import ru.itmo.bets.handler.repository.BetsRepository
import ru.itmo.bets.handler.request.CreateBetsGroupsRequestDto
import ru.itmo.bets.handler.request.CreateGroupRequestDto
import ru.itmo.bets.handler.service.BetsService
import ru.itmo.common.dto.BetDto
import ru.itmo.common.dto.BetGroupDto
import ru.itmo.common.exception.ResourceNotFoundException
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockitoExtension::class)
class BetsServiceMockTest {

    @Mock
    lateinit var betsRepository: BetsRepository

    @Mock
    lateinit var betsGroupsRepository: BetsGroupsRepository

    @InjectMocks
    lateinit var betsService: BetsService

    private lateinit var testBetEntity: BetsEntity
    private lateinit var testBetDto: BetDto
    private lateinit var testBetGroupDto: BetGroupDto
    private lateinit var testBetGroupEntity: BetsGroupsEntity

    @BeforeEach
    fun setup() {
        testBetEntity = BetsEntity(
            betId = 1L,
            amount = BigDecimal("50.00"),
            ratio = BigDecimal("2.00"),
            userId = 1L,
            availableBetId = 100L
        )

        testBetDto = BetDto(
            betId = 1L,
            amount = BigDecimal("50.00"),
            ratio = BigDecimal("2.00"),
            userId = 1L,
            availableBetId = 100L
        )

        testBetGroupEntity = BetsGroupsEntity(
            groupId = 1L,
            description = "Test Group"
        )

        testBetGroupDto = BetGroupDto(
            groupId = 1L,
            description = "Test Group"
        )
    }

    @Test
    fun `should return bet group DTO when group exists`() {
        // Arrange
        `when`(betsGroupsRepository.findById(1L)).thenReturn(Optional.of(testBetGroupEntity))

        // Act
        val result = betsService.getBetGroup(1L)

        // Assert
        assertThat(result).isNotNull
        assertThat(result?.groupId).isEqualTo(testBetGroupDto.groupId)
        assertThat(result?.description).isEqualTo(testBetGroupDto.description)
        verify(betsGroupsRepository, times(1)).findById(1L)
    }

    @Test
    fun `should return null when bet group does not exist`() {
        // Arrange
        `when`(betsGroupsRepository.findById(1L)).thenReturn(Optional.empty())

        // Act
        val result = betsService.getBetGroup(1L)

        // Assert
        assertThat(result).isNull()
        verify(betsGroupsRepository, times(1)).findById(1L)
    }

    @Test
    fun `should create new bet groups and return list of DTOs`() {
        // Arrange
        val createRequest = CreateBetsGroupsRequestDto(groups = listOf(CreateGroupRequestDto("")))
        `when`(betsGroupsRepository.save(any(BetsGroupsEntity::class.java))).thenReturn(testBetGroupEntity)

        // Act
        val result = betsService.createBetGroup(createRequest)

        // Assert
        assertThat(result).hasSize(1)
        assertThat(result[0].groupId).isEqualTo(testBetGroupDto.groupId)
        assertThat(result[0].description).isEqualTo(testBetGroupDto.description)
        verify(betsGroupsRepository, times(1)).save(any(BetsGroupsEntity::class.java))
    }

    @Test
    fun `should throw ResourceNotFoundException when deleting non-existent bet group`() {
        // Arrange
        `when`(betsGroupsRepository.findById(1L)).thenReturn(Optional.empty())

        // Act & Assert
        val exception = assertThrows<ResourceNotFoundException> {
            betsService.deleteBetGroup(1L)
        }

        assertThat(exception.message).isEqualTo("Bet group with id 1 not found")
        verify(betsGroupsRepository, times(1)).findById(1L)
        verify(betsGroupsRepository, times(0)).deleteById(anyLong())
    }

    @Test
    fun `should delete bet group by ID when it exists`() {
        // Arrange
        `when`(betsGroupsRepository.findById(1L)).thenReturn(Optional.of(testBetGroupEntity))

        // Act
        betsService.deleteBetGroup(1L)

        // Assert
        verify(betsGroupsRepository, times(2)).findById(1L)
        verify(betsGroupsRepository, times(1)).deleteById(1L)
    }

    @Test
    fun `should return paginated list of bet groups`() {
        // Arrange
        val pageable = PageRequest.of(0, 2, Sort.by("groupId"))
        val betGroupsPage = PageImpl(listOf(testBetGroupEntity, testBetGroupEntity))
        `when`(betsGroupsRepository.findAll(pageable)).thenReturn(betGroupsPage)

        // Act
        val result = betsService.getBetGroups(pageNumber = 0, pageSize = 2)

        // Assert
        assertThat(result.items).hasSize(2)
        assertThat(result.total).isEqualTo(2)
        assertThat(result.pageSize).isEqualTo(2)
        assertThat(result.page).isEqualTo(0)
        verify(betsGroupsRepository, times(1)).findAll(pageable)
    }

    @Test
    fun `should return list of bet DTOs for given available bet IDs`() {
        // Arrange
        val availableBetIds = listOf(100L, 101L)
        `when`(betsRepository.findAllByAvailableBetIdIn(availableBetIds)).thenReturn(listOf(testBetEntity, testBetEntity))

        // Act
        val result = betsService.getBetsByAvailableBetIds(availableBetIds)

        // Assert
        assertThat(result).hasSize(2)
        assertThat(result[0].betId).isEqualTo(testBetDto.betId)
        verify(betsRepository, times(1)).findAllByAvailableBetIdIn(availableBetIds)
    }

    @Test
    fun `should create and return a new bet`() {
        // Arrange
        val userId = 1L
        val bet = AvailableBetDto(
            availableBetId = 1L,
            ratio = 1.0.toBigDecimal(),
            betsClosed = false,
            groupId = 1L,
            matchId = 1L,
        )
        val ratio = BigDecimal("2.00")
        val amount = BigDecimal("50.00")
        `when`(betsRepository.save(any(BetsEntity::class.java))).thenReturn(testBetEntity)

        // Act
        val result = betsService.createBet(userId, bet, ratio, amount)

        // Assert
        assertThat(result.betId).isEqualTo(testBetDto.betId)
        assertThat(result.amount).isEqualTo(amount)
        assertThat(result.ratio).isEqualTo(ratio)
        verify(betsRepository, times(1)).save(any(BetsEntity::class.java))
    }

    @Test
    fun `should return paginated list of bets for a user`() {
        // Arrange
        val userId = 1L
        val pageable = PageRequest.of(0, 2)
        val betPage = PageImpl(listOf(testBetEntity, testBetEntity))
        `when`(betsRepository.findAllByUserId(userId, pageable)).thenReturn(betPage)

        // Act
        val result = betsService.getUserBets(userId, pageNumber = 0, pageSize = 2)

        // Assert
        assertThat(result.items).hasSize(2)
        assertThat(result.total).isEqualTo(2)
        assertThat(result.pageSize).isEqualTo(2)
        assertThat(result.page).isEqualTo(0)
        verify(betsRepository, times(1)).findAllByUserId(userId, pageable)
    }
}
