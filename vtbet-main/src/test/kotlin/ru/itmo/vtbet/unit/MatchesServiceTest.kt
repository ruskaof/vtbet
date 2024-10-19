package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportsEntity
import ru.itmo.vtbet.model.entity.AvailableBetsEntity
import ru.itmo.vtbet.model.entity.UsersAccountsEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.repository.UsersAccountsRepository
import ru.itmo.vtbet.service.MatchesService
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Optional

class MatchesServiceTest {
    private val matchesRepository = Mockito.mock(MatchesRepository::class.java)

    private val matchesService =
        MatchesService(matchesRepository)

    @Test
    fun `get matches`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)))

        Mockito.`when`(matchesRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        val result = matchesService.getMatches(PageRequest.of(pageNumber, pageSize))
        val expectedResult = listOf(
            MatchDto(
                matchId = sportId,
                name = matchName,
                SportDto(
                    sportId = sportId,
                    name = sportName,
                ),
                true
            )
        )
        Assertions.assertEquals(expectedResult, result.content)
    }

    @Test
    fun `get matches by sportId`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)))

        Mockito.`when`(matchesRepository.findAllBySportSportId(sportId, PageRequest.of(pageNumber, pageSize)))
            .thenReturn(pageSports)

        val result = matchesService.getMatches(sportId, PageRequest.of(pageNumber, pageSize))
        val expectedResult = listOf(
            MatchDto(
                matchId = sportId,
                name = matchName,
                SportDto(
                    sportId = sportId,
                    name = sportName,
                ),
                true
            )
        )
        Assertions.assertEquals(expectedResult, result.content)
    }

    @Test
    fun `save match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val name = "42"
        val ended = false
        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)

        val matchDto = MatchDto(
            matchId = matchId,
            name = name,
            sport = SportDto(
                sportId = sportId,
                name = sportName,
            ),
            ended = ended,
        )

        Mockito.`when`(matchesRepository.saveAndFlush(any())).thenReturn(matchesEntity)

        val result = matchesService.save(matchDto)


        val expectedResult = MatchDto(
            matchId,
            matchName,
            SportDto(matchId, sportName),
            true
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `update match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val name = "42"
        val ended = true

        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)

        val matchDto = MatchDto(
            matchId = matchId,
            name = name,
            sport = SportDto(
                sportId = sportId,
                name = sportName,
            ),
            ended = ended,
        )

        Mockito.`when`(matchesRepository.saveAndFlush(any())).thenReturn(matchesEntity)


        val result = matchesService.update(matchDto)

        val expectedResult = MatchDto(
            matchId,
            matchName,
            SportDto(matchId, sportName),
            true
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)


        Mockito.`when`(matchesRepository.findById(any())).thenReturn(Optional.of(matchesEntity))

        val result = matchesService.getMatch(matchId)


        val expectedResult = MatchDto(
            matchId,
            matchName,
            SportDto(matchId, sportName),
            true
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `delete match`() {
        val matchId = 1L

        matchesService.delete(matchId)

        verify(matchesRepository).deleteById(any())
    }

    @Test
    fun `end match`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val matchesEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), false)

        Mockito.`when`(matchesRepository.findById(any())).thenReturn(Optional.of(matchesEntity))


        matchesService.endMatch(matchId)


        verify(matchesRepository).save(matchesEntity.copy(ended = true))
    }

    @Test
    fun `end match with exception`() {
        val matchId = 1L

        Mockito.`when`(matchesRepository.findById(any())).thenReturn(Optional.empty())

        assertThrows<ResourceNotFoundException> { matchesService.endMatch(matchId) }

    }

    //    @Test
//    fun `get bets by match id`() {
//        val typeOfBetMatchId = 1L
//        val description = "team 1 win"
//        val matchId = 1L
//        val matchName = "El Clasico"
//        val sportId = 1L
//        val sportName = "football"
//        val pageSize = 20
//        val pageNumber = 1
//        val ratioNow = BigDecimal(2)
//        val pageSports = PageImpl(
//            listOf(
//                AvailableBetsEntity(
//                    matchId,
//                    ratioNow,
//                    MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true),
//                    TypeOfBetEntity(typeOfBetMatchId, description, BetsGroupsEntity(1L))
//                )
//            )
//        )
//
//        Mockito.`when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize)))
//            .thenReturn(pageSports)
//        val result = matchesService.getBetsByMatchId(matchId, pageNumber, pageSize)
//        val expectedResult = PagingDto(
//            listOf(
//                SimpleAvailableBetsDto(
//                    availableBetId = sportId,
//                    ratio = ratioNow,
//                    BetGroup(
//                        id = sportId,
//                        description = description,
//                    ),
//                    matchId
//                )
//            ),
//            1,
//            pageNumber,
//            pageSize
//        )
//        Assertions.assertEquals(expectedResult, result)
//    }
//
//
//    @Test
//    fun `endMatch updates match and user accounts successfully`() {
//        val userId = 1L
//        val id = 2L
//        val ratio = BigDecimal(2)
//        val amount = BigDecimal(100)
//        val sportId = 1L
//        val sportName = "football"
//        val matchId = 1L
//        val matchName = "El Clasico"
//
//        val user = UsersEntity(userId, OffsetDateTime.now().toInstant())
//        val matchEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), false)
//        val typeOfBet = TypeOfBetEntity(
//            id = 1,
//            description = "",
//            betGroupEntity = BetsGroupsEntity(
//                betGroupId = 1,
//            ),
//        )
//        val typeOfBetMatch =
//            AvailableBetsEntity(id = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)
//        val winningBet = BetsEntity(id, amount, ratio, user, typeOfBetMatch)
//        val losingBet = BetsEntity(id + 1, amount, ratio, user, typeOfBetMatch)
//
//        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchEntity))
//        Mockito.`when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId)).thenReturn(listOf(typeOfBetMatch))
//        Mockito.`when`(betsRepository.findByTypeOfBetMatchTypeOfBetMatchIdIn(listOf(typeOfBetMatch.id!!)))
//            .thenReturn(listOf(winningBet, losingBet))
//
//        val winningUserAccount =
//            UsersAccountsEntity(
//                userId = userId,
//                balanceAmount = BigDecimal.TEN,
//                username = "username",
//                email = "email@email.com",
//                phoneNumber = null,
//                accountVerified = true,
//            )
//        Mockito.`when`(usersAccountsRepository.findById(winningBet.usersEntity.id!!))
//            .thenReturn(Optional.of(winningUserAccount))
//        matchesService.endMatch(matchId, setOf(id))
//
//        verify(matchesRepository).findById(matchId)
//        verify(typeOfBetMatchRepository).findAllByMatchMatchId(matchId)
//        verify(usersAccountsRepository).findById(ArgumentMatchers.eq(winningBet.usersEntity.id!!))
//        verify(usersAccountsRepository).save(winningUserAccount)
//        verify(matchesRepository).save(matchEntity.copy(ended = true))
//    }
//
//    @Test
//    fun `endMatch updates match that has been already ended`() {
//        val id = 2L
//        val sportId = 1L
//        val sportName = "football"
//        val matchId = 1L
//        val matchName = "El Clasico"
//
//        val matchEntity = MatchesEntity(matchId, matchName, SportsEntity(sportId, sportName), true)
//
//        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchEntity))
//        assertThrows<ResourceNotFoundException> { matchesService.endMatch(matchId, setOf(id)) }
//    }
//
//    @Test
//    fun `delete match`() {
//        val matchId = 1L
//        Mockito.`when`(matchesRepository.existsById(matchId)).thenReturn(true)
//        matchesService.delete(matchId)
//        Mockito.verify(matchesRepository).deleteById(
//            matchId
//        )
//    }
//
//    @Test
//    fun `delete unknown match`() {
//        val matchId = 1L
//        Mockito.`when`(matchesRepository.existsById(matchId)).thenReturn(false)
//        assertThrows<ResourceNotFoundException> { matchesService.delete(matchId) }
//    }
}
