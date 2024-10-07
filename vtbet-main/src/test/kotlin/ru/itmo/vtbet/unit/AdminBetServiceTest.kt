package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.request.CreateBetGroupRequest
import ru.itmo.vtbet.model.request.TypeOfBetDto
import ru.itmo.vtbet.repository.BetGroupRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository
import ru.itmo.vtbet.service.AdminBetService
import kotlin.random.Random

class AdminBetServiceTest {

    private val betGroupRepository: BetGroupRepository = mock()
    private val typeOfBetRepository: TypeOfBetRepository = mock()
    private val typeOfBetMatchRepository: TypeOfBetMatchRepository = mock()
    private val matchesRepository: MatchesRepository = mock()
    private val adminBetService = AdminBetService(
        betGroupRepository = betGroupRepository,
        typeOfBetRepository = typeOfBetRepository,
        typeOfBetMatchRepository = typeOfBetMatchRepository,
        matchesRepository = matchesRepository
    )

//    @Test
//    fun `create bet group`() {
//        // given
//        val request = CreateBetGroupRequest(
//            typeOfBets = listOf(
//                TypeOfBetDto("type1"),
//                TypeOfBetDto("type2"),
//            )
//        )
//        `when`(betGroupRepository.save(BetGroupEntity())).thenReturn(BetGroupEntity(betGroupId = 1))
//            .thenReturn(BetGroupEntity(betGroupId = 2))
//            .thenReturn(BetGroupEntity(betGroupId = 3))
//
//        // when
//        val result = adminBetService.createBetGroup(request)
//
//        // then
//        val expectedResul = Bet
//    }
}
