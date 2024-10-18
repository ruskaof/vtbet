package ru.itmo.vtbet.unit

import org.junit.Test
import org.junit.jupiter.api.Assertions
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.dto.BetDto
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.dto.UserDto
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.CreateBetsGroupsRequestDto
import ru.itmo.vtbet.model.request.CreateGroupRequestDto
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.service.BetsService
import java.math.BigDecimal
import java.time.Instant
import java.util.*


class BetsServiceTest {
    private val betsGroupsRepository = Mockito.mock(BetsGroupsRepository::class.java)
    private val betsRepository = Mockito.mock(BetsRepository::class.java)

    private val betsService =
        BetsService(
            betsRepository = betsRepository,
            betsGroupsRepository = betsGroupsRepository,
        )

    @Test
    fun getBetGroup() {
        val betGroupId = 1L
        val betsGroupsEntity = BetsGroupsEntity(betGroupId, "description")

        val expectedBetGroupDto = BetGroupDto(
            groupId = betGroupId,
            description = "description",
        )

        Mockito.`when`(betsGroupsRepository.findById(betGroupId)).thenReturn(Optional.of(betsGroupsEntity))

        val betGroupDto = betsService.getBetGroup(betGroupId)

        Assertions.assertEquals(expectedBetGroupDto, betGroupDto)
    }

    @Test
    fun createBetGroup() {
        val betGroupId = 1L
        val betsGroupsEntity = BetsGroupsEntity(betGroupId, "description")

        val createBetsGroupsRequestDto = CreateBetsGroupsRequestDto(
            listOf(CreateGroupRequestDto("description"))
        )

        val expectedBetGroupDto = listOf(
            BetGroupDto(
                groupId = betGroupId,
                description = "description",
            )
        )

        Mockito.`when`(betsGroupsRepository.save(any())).thenReturn(betsGroupsEntity)

        val listBetGroupDto = betsService.createBetGroup(createBetsGroupsRequestDto)

        Assertions.assertEquals(expectedBetGroupDto, listBetGroupDto)
    }

    @Test
    fun getBetsByAvailableBetIds() {
        val betId = 1L
        val availableBetId = 1L
        val amount = BigDecimal(100)
        val ratio = BigDecimal(1.5)

        val userId = 1L
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val phoneNumber = "79991077751"
        val accountVerified = true
        val registrationDate = Instant.now()

        val betsEntity = listOf(
            BetsEntity(
                betId = betId,
                amount = amount,
                availableBetId = availableBetId,
                ratio = ratio,
                usersEntity = UsersEntity(
                    userId = userId,
                    username = username,
                    email = email,
                    phoneNumber = phoneNumber,
                    accountVerified = accountVerified,
                    registrationDate = registrationDate,
                )
            )
        )
        val expectedBetDto = listOf(
            BetDto(
                betId = betId,
                ratio = ratio,
                amount = amount,
                userId = userId,
                availableBetId = availableBetId,
            )
        )

        Mockito.`when`(betsRepository.findAllByAvailableBetIdIn(listOf(betId))).thenReturn(betsEntity)

        val listBetDto = betsService.getBetsByAvailableBetIds(listOf(betId))

        Assertions.assertEquals(expectedBetDto, listBetDto)
    }

    @Test
    fun createBet() {
        val betId = 1L
        val availableBetId = 1L
        val amount = BigDecimal(100)
        val ratio = BigDecimal(1.5)
        val groupId = 1L
        val userId = 1L
        val username = "Dima"
        val email = "niktog1@mail.ru"
        val phoneNumber = "79991077751"
        val accountVerified = true
        val registrationDate = Instant.now()
        val betsClosed = false
        val matchId = 1L

        val betsEntity = BetsEntity(
            betId = betId,
            amount = amount,
            availableBetId = availableBetId,
            ratio = ratio,
            usersEntity = UsersEntity(
                userId = userId,
                username = username,
                email = email,
                phoneNumber = phoneNumber,
                accountVerified = accountVerified,
                registrationDate = registrationDate,
            )
        )
        val userDto = UserDto(
            userId = userId,
            username = username,
            email = email,
            phoneNumber = phoneNumber,
            accountVerified = accountVerified,
            registrationDate = registrationDate,
        )
        val availableBetDto = AvailableBetDto(
            availableBetId = availableBetId,
            ratio = ratio,
            groupId = groupId,
            betsClosed = betsClosed,
            matchId = matchId,
        )

        val expectedBetDto = BetDto(
            betId = betId,
            ratio = ratio,
            amount = amount,
            userId = userId,
            availableBetId = availableBetId,
        )

        Mockito.`when`(betsRepository.save(any())).thenReturn(betsEntity)

        val betDto = betsService.createBet(userDto, availableBetDto, ratio, amount)

        Assertions.assertEquals(expectedBetDto, betDto)
    }

}