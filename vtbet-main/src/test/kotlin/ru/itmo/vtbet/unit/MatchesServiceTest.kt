package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SimpleTypeOfBetMatchDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.dto.TypeOfBetDto
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.entity.UserAccountEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.UpdateMatchRequest
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.UserAccountRepository
import ru.itmo.vtbet.service.MatchesService
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Optional

class MatchesServiceTest {
    private val matchesRepository = Mockito.mock(MatchesRepository::class.java)
    private val betsRepository = Mockito.mock(BetsRepository::class.java)
    private val userAccountRepository = Mockito.mock(UserAccountRepository::class.java)
    private val typeOfBetMatchRepository = Mockito.mock(TypeOfBetMatchRepository::class.java)
    private val matchesService =
        MatchesService(matchesRepository, betsRepository, userAccountRepository, typeOfBetMatchRepository)

    @Test
    fun `get matches`() {
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val pageSports = PageImpl(listOf(MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)))

        Mockito.`when`(matchesRepository.findAll(PageRequest.of(pageNumber, pageSize))).thenReturn(pageSports)

        val result = matchesService.getMatches(pageNumber, pageSize)
        val expectedResult = PagingDto(
            listOf(
                MatchDto(
                    id = sportId,
                    name = matchName,
                    SportDto(
                        id = sportId,
                        name = sportName,
                    ),
                    true
                )
            ),
            1,
            pageNumber,
            pageSize
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `get bets by match id`() {
        val typeOfBetMatchId = 1L
        val description = "team 1 win"
        val matchId = 1L
        val matchName = "El Clasico"
        val sportId = 1L
        val sportName = "football"
        val pageSize = 20
        val pageNumber = 1
        val ratioNow = BigDecimal(2)
        val pageSports = PageImpl(
            listOf(
                TypeOfBetMatchEntity(
                    matchId,
                    ratioNow,
                    MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true),
                    TypeOfBetEntity(typeOfBetMatchId, description, BetGroupEntity(1L))
                )
            )
        )

        Mockito.`when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize)))
            .thenReturn(pageSports)
        val result = matchesService.getBetsByMatchId(matchId, pageNumber, pageSize)
        val expectedResult = PagingDto(
            listOf(
                SimpleTypeOfBetMatchDto(
                    id = sportId,
                    ratioNow = ratioNow,
                    TypeOfBetDto(
                        id = sportId,
                        description = description,
                    ),
                    matchId
                )
            ),
            1,
            pageNumber,
            pageSize
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
        val matchesEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)
        val matches2Entity = MatchesEntity(matchId, name, SportEntity(sportId, sportName), true)

        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchesEntity))
        Mockito.`when`(matchesRepository.save(matches2Entity)).thenReturn(matches2Entity)
        val result = matchesService.updateMatch(UpdateMatchRequest(name), matchId)
        val expectedResult = MatchDto(
            matchId,
            name,
            SportDto(matchId, sportName),
            true
        )
        Assertions.assertEquals(expectedResult, result)
    }

    @Test
    fun `endMatch updates match and user accounts successfully`() {
        val userId = 1L
        val id = 2L
        val ratio = BigDecimal(2)
        val amount = BigDecimal(100)
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"

        val user = UsersEntity(userId, OffsetDateTime.now().toInstant())
        val matchEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), false)
        val typeOfBet = TypeOfBetEntity(
            typeOfBetId = 1,
            description = "",
            betGroupEntity = BetGroupEntity(
                betGroupId = 1,
            ),
        )
        val typeOfBetMatch =
            TypeOfBetMatchEntity(typeOfBetMatchId = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)
        val winningBet = BetsEntity(id, amount, ratio, user, typeOfBetMatch)
        val losingBet = BetsEntity(id + 1, amount, ratio, user, typeOfBetMatch)

        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchEntity))
        Mockito.`when`(typeOfBetMatchRepository.findAllByMatchMatchId(matchId)).thenReturn(listOf(typeOfBetMatch))
        Mockito.`when`(betsRepository.findByTypeOfBetMatchTypeOfBetMatchIdIn(listOf(typeOfBetMatch.typeOfBetMatchId!!)))
            .thenReturn(listOf(winningBet, losingBet))

        val winningUserAccount =
            UserAccountEntity(
                userId = userId,
                balanceAmount = BigDecimal.TEN,
                username = "username",
                email = "email@email.com",
                phoneNumber = null,
                accountVerified = true,
            )
        Mockito.`when`(userAccountRepository.findById(winningBet.usersEntity.id!!))
            .thenReturn(Optional.of(winningUserAccount))
        matchesService.endMatch(matchId, setOf(id))

        verify(matchesRepository).findById(matchId)
        verify(typeOfBetMatchRepository).findAllByMatchMatchId(matchId)
        verify(userAccountRepository).findById(ArgumentMatchers.eq(winningBet.usersEntity.id!!))
        verify(userAccountRepository).save(winningUserAccount)
        verify(matchesRepository).save(matchEntity.copy(ended = true))
    }

    @Test
    fun `endMatch updates match that has been already ended`() {
        val id = 2L
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"

        val matchEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)

        Mockito.`when`(matchesRepository.findById(matchId)).thenReturn(Optional.of(matchEntity))
        assertThrows<IllegalArgumentException> { matchesService.endMatch(matchId, setOf(id)) }
    }

    @Test
    fun `delete match`() {
        val matchId = 1L
        Mockito.`when`(matchesRepository.existsById(matchId)).thenReturn(true)
        matchesService.delete(matchId)
        Mockito.verify(matchesRepository).deleteById(
            matchId
        )
    }
}
