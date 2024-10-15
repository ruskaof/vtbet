package ru.itmo.vtbet.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import ru.itmo.vtbet.model.entity.AvailableBetsEntity
import ru.itmo.vtbet.model.entity.BetsGroupsEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportsEntity
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.repository.BetsGroupsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportsRepository

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MatchesControllerTest : BaseIntegrationTest() {
    @Autowired
    private lateinit var availableBetsRepository: AvailableBetsRepository

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var sportsRepository: SportsRepository

    @Autowired
    lateinit var matchesRepository: MatchesRepository

    @Autowired
    lateinit var typeOfBetMatchesRepository: AvailableBetsRepository

    @Autowired
    lateinit var betsGroupsRepository: BetsGroupsRepository

    @Test
    fun `getMatches test`() {
        val sport = sportsRepository.save(
            SportsEntity(
                sportName = "test sport",
            )
        )
        val match = matchesRepository.save(
            MatchesEntity(
                matchName = "test match",
                sport = sport,
                ended = false,
            )
        )
        val pageNumber = 0
        val pageSize = 3

        mockMvc.perform(get("/matches?pageNumber=$pageNumber&pageSize=$pageSize"))
            .andExpect(status().isOk)

            .andExpect(header().exists("X-Total-Count"))
            .andExpect(header().exists("X-Current-Page"))
            .andExpect(header().exists("X-Page-Size"))
    }

    @Test
    fun `getBetsByMatchId test`() {
        val sport = sportsRepository.save(
            SportsEntity(
                sportName = "test sport",
            )
        )
        val match = matchesRepository.save(
            MatchesEntity(
                matchName = "test match",
                sport = sport,
                ended = false,
            )
        )

        val betGroup = betsGroupsRepository.save(
            BetsGroupsEntity(description = "some description")
        )

        val availableBet = availableBetsRepository.save(
            AvailableBetsEntity(
                ratio = 1.0.toBigDecimal(),
                matchId = match.matchId!!,
                betsGroupsEntity = BetsGroupsEntity(
                    groupId = betGroup.groupId!!,
                    description = betGroup.description,
                ),
                betsClosed = false,
            )
        )

        val pageNumber = 0
        val pageSize = 5

        mockMvc.perform(
            get("/matches/${match.matchId}/bets")
                .param("pageNumber", pageNumber.toString())
                .param("pageSize", pageSize.toString())
        )
            .andExpect(status().isOk)

            .andExpect(header().exists("X-Total-Count"))
            .andExpect(header().exists("X-Current-Page"))
            .andExpect(header().exists("X-Page-Size"))

            .andExpect(jsonPath("$[0].id").value(availableBet.availableBetId))
            .andExpect(jsonPath("$[0].ratio").value(availableBet.ratio))
            .andExpect(jsonPath("$[0].match_id").value(availableBet.matchId))
            .andExpect(jsonPath("$[0].group_id").value(availableBet.betsGroupsEntity.groupId))
            .andExpect(jsonPath("$[0].bets_closed").value(availableBet.betsClosed))
    }

    @Test
    fun `updateMatch test`() {
        val sport = sportsRepository.save(
            SportsEntity(
                sportName = "test sport",
            )
        )
        val match = matchesRepository.save(
            MatchesEntity(
                matchName = "test match",
                sport = sport,
                ended = false,
            )
        )

        val betGroup = betsGroupsRepository.save(
            BetsGroupsEntity(description = "some description")
        )

        val updateMatchRequestDto = UpdateMatchRequestDto(name = "new name")

        mockMvc.perform(
            patch("/matches/${match.matchId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMatchRequestDto))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(match.matchId))
            .andExpect(jsonPath("$.name").value("new name"))
    }

    @Test
    fun `endMatch test`() {
        val sport = sportsRepository.save(
            SportsEntity(
                sportName = "test sport",
            )
        )
        val match = matchesRepository.save(
            MatchesEntity(
                matchName = "test match",
                sport = sport,
                ended = false,
            )
        )

        val betGroup = betsGroupsRepository.save(
            BetsGroupsEntity(description = "some description")
        )

        val availableBet = availableBetsRepository.save(
            AvailableBetsEntity(
                ratio = 1.0.toBigDecimal(),
                matchId = match.matchId!!,
                betsGroupsEntity = BetsGroupsEntity(
                    groupId = betGroup.groupId!!,
                    description = betGroup.description,
                ),
                betsClosed = false,
            )
        )

        val successfulBets = setOf(1L, 2L, 3L)
        mockMvc.perform(
            post("/matches/${match.matchId}/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(successfulBets))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteMatch test`() {
        val sport = sportsRepository.save(
            SportsEntity(
                sportName = "test sport",
            )
        )
        val match = matchesRepository.save(
            MatchesEntity(
                matchName = "test match",
                sport = sport,
                ended = false,
            )
        )

        mockMvc.perform(
            delete("/match/${match.matchId}")
        )
            .andExpect(status().isNoContent)
    }
}
