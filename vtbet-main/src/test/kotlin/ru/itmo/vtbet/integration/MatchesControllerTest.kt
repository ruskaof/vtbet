package ru.itmo.vtbet.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.itmo.vtbet.model.entity.BetGroupEntity
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.entity.TypeOfBetEntity
import ru.itmo.vtbet.model.entity.AvailableBetEntity
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto
import ru.itmo.vtbet.repository.BetGroupRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportRepository
import ru.itmo.vtbet.repository.AvailableBetRepository
import ru.itmo.vtbet.repository.TypeOfBetRepository

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class MatchesControllerTest : BaseIntegrationTest() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var sportRepository: SportRepository

    @Autowired
    lateinit var matchesRepository: MatchesRepository

    @Autowired
    lateinit var typeOfBetRepository: TypeOfBetRepository

    @Autowired
    lateinit var typeOfBetMatchesRepository: AvailableBetRepository

    @Autowired
    lateinit var betGroupRepository: BetGroupRepository

    @Test
    fun `getMatches test`() {
        val sport = sportRepository.save(
            SportEntity(
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
        val sport = sportRepository.save(
            SportEntity(
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

        val betGroup = betGroupRepository.save(
            BetGroupEntity()
        )

        val typeOfBet = typeOfBetRepository.save(
            TypeOfBetEntity(
                description = "test type",
                betGroupEntity = betGroup
            )
        )

        val typeOfBetMatch = typeOfBetMatchesRepository.save(
            AvailableBetEntity(
                ratioNow = 1.0.toBigDecimal(),
                match = match,
                typeOfBets = typeOfBet,
            )
        )

        val pageNumber = 0
        val pageSize = 5

        mockMvc.perform(
            get("/match/${match.matchId}/bets")
                .param("pageNumber", pageNumber.toString())
                .param("pageSize", pageSize.toString())
        )
            .andExpect(status().isOk)

            .andExpect(header().exists("X-Total-Count"))
            .andExpect(header().exists("X-Current-Page"))
            .andExpect(header().exists("X-Page-Size"))

            .andExpect(jsonPath("$[0].id").value(typeOfBetMatch.id))
            .andExpect(jsonPath("$[0].ratio_now").value(typeOfBetMatch.ratioNow))
            .andExpect(jsonPath("$[0].match_id").value(typeOfBetMatch.match.matchId))
            .andExpect(jsonPath("$[0].type_of_bet_description").value(typeOfBetMatch.typeOfBets.description))
            .andExpect(jsonPath("$[0].type_of_bet_id").value(typeOfBetMatch.typeOfBets.id))
    }

    @Test
    fun `updateMatch test`() {
        val sport = sportRepository.save(
            SportEntity(
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

        val betGroup = betGroupRepository.save(
            BetGroupEntity()
        )

        val typeOfBet = typeOfBetRepository.save(
            TypeOfBetEntity(
                description = "test type",
                betGroupEntity = betGroup
            )
        )

        val typeOfBetMatch = typeOfBetMatchesRepository.save(
            AvailableBetEntity(
                ratioNow = 1.0.toBigDecimal(),
                match = match,
                typeOfBets = typeOfBet,
            )
        )

        val updateMatchRequestDto = UpdateMatchRequestDto(name = "new name")

        mockMvc.perform(
            patch("/match/${match.matchId}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMatchRequestDto))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(match.matchId))
            .andExpect(jsonPath("$.name").value("new name"))
    }

    @Test
    fun `endMatch test`() {
        val sport = sportRepository.save(
            SportEntity(
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

        val betGroup = betGroupRepository.save(
            BetGroupEntity()
        )

        val typeOfBet = typeOfBetRepository.save(
            TypeOfBetEntity(
                description = "test type",
                betGroupEntity = betGroup
            )
        )

        typeOfBetMatchesRepository.save(
            AvailableBetEntity(
                ratioNow = 1.0.toBigDecimal(),
                match = match,
                typeOfBets = typeOfBet,
            )
        )

        val successfulBets = setOf(1L, 2L, 3L)
        mockMvc.perform(
            post("/match/${match.matchId}/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(successfulBets))
        )
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteMatch test`() {
        val sport = sportRepository.save(
            SportEntity(
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
