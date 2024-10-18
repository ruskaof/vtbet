package ru.itmo.vtbet.unit

import org.mockito.Mockito.mock
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.service.*

class AdminBetsServiceTest {
    private val betsGroupsRepository = mock(BetsGroupsRepository::class.java)
    private val availableBetsRepository = mock(AvailableBetsServiceTest::class.java)
    private val matchesRepository = mock(MatchesRepository::class.java)
    private val matchesService = mock(MatchesService::class.java)
    private val betsService = mock(BetsService::class.java)

//    private val adminBetService =
//        AdminBetService(
//            matchesService = matchesService,
//            betsService = betsService,
//            availableBetsService = availableBetsRepository,
//            )

//    @Test
//    fun createBetGroup() {
//        val betGroupId = 1L
//        val betsGroupsEntity = BetsGroupsEntity(betGroupId, "description")
//
//        val createBetsGroupsRequestDto =
//            CreateBetsGroupsRequestDto(listOf(CreateGroupRequestDto("description1"), CreateGroupRequestDto("description2")))
//
//        val expectedBetGroupDto = BetGroupDto(
//            groupId = betGroupId,
//            description = "description",
//        )
//
//        Mockito.`when`(betsGroupsRepository.save(any(BetsGroupsEntity::class.java))).thenReturn(betsGroupsEntity)
//        Mockito.`when`(typeOfBetRepository.save(any(TypeOfBetEntity::class.java))).thenReturn(
//            typeOfBets[0],
//            typeOfBets[1],
//        )
//
//        val betGroupDto = adminBetService.createAvailableBet(createBetsGroupsRequestDto)
//
//        assertEquals(expectedBetGroupDto, betGroupDto)
//
//        val inOrder = inOrder(betsGroupsRepository, typeOfBetRepository)
//        inOrder.verify(betsGroupsRepository).save(any(BetsGroupsEntity::class.java))
//        inOrder.verify(typeOfBetRepository, times(2)).save(any(TypeOfBetEntity::class.java))
//    }
//
//    @Test
//    fun `updateBetMatch updates successful when valid id`() {
//        val id = 2L
//        val ratio = BigDecimal(2)
//        val sportId = 1L
//        val sportName = "football"
//        val matchId = 1L
//        val matchName = "El Clasico"
//
//        val matchEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), false)
//        val typeOfBet = TypeOfBetEntity(
//            id = 1,
//            description = "",
//            betGroupEntity = BetsGroupsEntity(
//                betGroupId = 1,
//            ),
//        )
//        val newRatio = 1.1
//        val request = UpdateAvailableBetRequestDto(newRatio)
//        val oldTypeOfBetMatch =
//            AvailableBetsEntity(id = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)
//
//        Mockito.`when`(availableBetsRepository.findById(id)).thenReturn(Optional.of(oldTypeOfBetMatch))
//        Mockito.`when`(availableBetsRepository.save(any()))
//            .thenReturn(oldTypeOfBetMatch.copy(ratioNow = newRatio.toBigDecimal()))
//
//        adminBetService.updateAvailableBet(id, request)
//
//        verify(availableBetsRepository).findById(id)
//        verify(availableBetsRepository).save(any())
//    }
//
//    @Test
//    fun `createTypeOfBetMatch creates bet match successfully`() {
//        val id = 2L
//        val ratio = BigDecimal(2)
//        val sportId = 1L
//        val sportName = "football"
//        val matchId = 1L
//        val matchName = "El Clasico"
//
//        val matchEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), false)
//        val typeOfBet = TypeOfBetEntity(
//            id = 1,
//            description = "",
//            betGroupEntity = BetsGroupsEntity(
//                betGroupId = 1,
//            ),
//        )
//        val typeOfBetMatch = AvailableBetsEntity(id = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)
//        val typeOfBetId = 1L
//        val createRequest = CreateAvailableBetRequestDto(typeOfBetId, 1.1.toBigDecimal())
//
//        Mockito.`when`(typeOfBetRepository.findById(typeOfBetId)).thenReturn(Optional.of(typeOfBet))
//        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchEntity))
//        Mockito.`when`(availableBetsRepository.save(any())).thenReturn(typeOfBetMatch)
//
//        val createdTypeOfBetMatch = adminBetService.createAvailableBet(createRequest, matchId)
//
//        verify(typeOfBetRepository).findById(typeOfBetId)
//        verify(matchesRepository).findById(matchId)
//        verify(availableBetsRepository).save(any())
//
//        assertEquals(createdTypeOfBetMatch.id, typeOfBetMatch.id)
//        assertEquals(createdTypeOfBetMatch.ratioNow, typeOfBetMatch.ratioNow)
//    }
}
