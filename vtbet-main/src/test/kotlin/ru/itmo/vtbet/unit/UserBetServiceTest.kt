package ru.itmo.vtbet.unit

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.mock
import ru.itmo.vtbet.exception.IllegalBetException
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.entity.UserAccountEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.MakeBetRequest
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.UserAccountRepository
import ru.itmo.vtbet.repository.UsersRepository
import ru.itmo.vtbet.service.UserBetService
import ru.itmo.vtbet.service.toDto
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.Optional

class UserBetServiceTest {
    private val usersRepository = mock(UsersRepository::class.java)
    private val userAccountRepository = mock(UserAccountRepository::class.java)
    private val betRepository = mock(BetsRepository::class.java)
    private val typeOfBetMatchRepository = mock(TypeOfBetMatchRepository::class.java)

    private val userBetService =
        UserBetService(usersRepository, userAccountRepository, betRepository, typeOfBetMatchRepository)

    @Test
    fun makeBetWithEnoughBalance() {
        val userId = 1L
        val id = 2L
        val ratio = BigDecimal(2)
        val amount = BigDecimal(100)
        val balance = amount * BigDecimal(2)
        val makeBetRequest = MakeBetRequest(userId, ratio, amount)
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"

        val userAccount = UserAccountEntity(userId, balance, "John", "john@doe.com", "12345678", true)
        val user = UsersEntity(userId, OffsetDateTime.now().toInstant())
        val matchEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)
        val typeOfBet = TypeOfBetEntity(
            typeOfBetId = 1,
            description = "",
            betGroupEntity = BetGroupEntity(
                betGroupId = 1,
            ),
        )
        val typeOfBetMatch =
            TypeOfBetMatchEntity(typeOfBetMatchId = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)
        val expectedBet = BetsEntity(id, amount, ratio, user, typeOfBetMatch)

        Mockito.`when`(userAccountRepository.findById(makeBetRequest.userId)).thenReturn(Optional.of(userAccount))
        Mockito.`when`(usersRepository.findById(makeBetRequest.userId)).thenReturn(Optional.of(user))
        Mockito.`when`(typeOfBetMatchRepository.findById(id)).thenReturn(Optional.of(typeOfBetMatch))

        val betDto = userBetService.makeBet(id, makeBetRequest)

        assertEquals(expectedBet.toDto(), betDto)

        val expectedUserAccount = userAccount.copy(balanceAmount = userAccount.balanceAmount - makeBetRequest.amount)
        val inOrder = inOrder(userAccountRepository, usersRepository, typeOfBetMatchRepository)
        inOrder.verify(userAccountRepository).findById(makeBetRequest.userId)
        inOrder.verify(usersRepository).findById(makeBetRequest.userId)
        inOrder.verify(typeOfBetMatchRepository).findById(id)
        inOrder.verify(userAccountRepository).save(expectedUserAccount)
    }

    @Test
    fun makeBetWithNotEnoughBalance() {
        val userId = 1L
        val id = 2L
        val ratio = BigDecimal(2)
        val amount = BigDecimal(100)
        val makeBetRequest = MakeBetRequest(userId, ratio, amount)
        val sportId = 1L
        val sportName = "football"
        val matchId = 1L
        val matchName = "El Clasico"

        val userAccount = UserAccountEntity(userId, amount * BigDecimal(0.5), "John", "john@doe.com", "12345678", true)
        val user = UsersEntity(userId, OffsetDateTime.now().toInstant())
        val matchEntity = MatchesEntity(matchId, matchName, SportEntity(sportId, sportName), true)
        val typeOfBet = TypeOfBetEntity(
            typeOfBetId = 1,
            description = "",
            betGroupEntity = BetGroupEntity(
                betGroupId = 1,
            ),
        )
        val typeOfBetMatch =
            TypeOfBetMatchEntity(typeOfBetMatchId = id, ratioNow = ratio, match = matchEntity, typeOfBets = typeOfBet)

        Mockito.`when`(userAccountRepository.findById(makeBetRequest.userId)).thenReturn(Optional.of(userAccount))
        Mockito.`when`(usersRepository.findById(makeBetRequest.userId)).thenReturn(Optional.of(user))
        Mockito.`when`(typeOfBetMatchRepository.findById(id)).thenReturn(Optional.of(typeOfBetMatch))

        assertThrows<IllegalBetException> { userBetService.makeBet(id, makeBetRequest) }
    }
}
