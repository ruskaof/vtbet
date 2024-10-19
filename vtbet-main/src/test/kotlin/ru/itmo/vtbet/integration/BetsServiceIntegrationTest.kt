package ru.itmo.vtbet.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.entity.BetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.entity.UsersEntity
import ru.itmo.vtbet.model.request.CreateBetsGroupsRequestDto
import ru.itmo.vtbet.model.request.CreateGroupRequestDto
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.UsersRepository
import ru.itmo.vtbet.service.BetsService
import ru.itmo.vtbet.service.toDto
import java.math.BigDecimal
import java.time.Instant
import kotlin.test.assertEquals

@SpringBootTest
@ActiveProfiles("test")
class BetsServiceIntegrationTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var betsService: BetsService

    @Autowired
    private lateinit var betsRepository: BetsRepository

    @Autowired
    private lateinit var usersRepository: UsersRepository

    @Autowired
    private lateinit var betsGroupsRepository: BetsGroupsRepository

    @Test
    fun getBetGroup() {
        val group = betsGroupsRepository.saveAndFlush(
            BetsGroupsEntity(
                description = "test"
            )
        )

        val result = betsService.getBetGroup(group.groupId!!)

        assertEquals(group.toDto(), result)
    }

    @Test
    fun createBetGroup() {
        val request = CreateBetsGroupsRequestDto(
            groups = listOf(
                CreateGroupRequestDto("test1"),
                CreateGroupRequestDto("test2"),
            )
        )

        val result = betsService.createBetGroup(request)

        assertEquals(request.groups.size, result.size)
        assertEquals(request.groups[0].description, result[0].description)
        assertEquals(request.groups[1].description, result[1].description)
    }
}