package ru.itmo.vtbet.integration

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.testcontainers.shaded.org.hamcrest.Matchers.hasSize
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.request.UpdateMatchRequest
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportRepository

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

    @Test
    fun getMatches() {
        val sport = SportEntity(
            sportId = 1L,
            sportName = "test sport",
        )
        sportRepository.save(
            sport
        )
        matchesRepository.save(
            MatchesEntity(
                matchId = 1L,
                matchName = "test match",
                sport = sport,
                ended = false,
            )
        )
        val pageNumber = 0
        val pageSize = 3

        mockMvc.perform(get("/matches?pageNumber=$pageNumber&pageSize=$pageSize"))
            .andExpect(status().isOk)
            .andExpect(header().exists("x-total-elements"))
            .andExpect(header().exists("x-total-pages"))
            .andExpect(header().exists("x-current-page"))
            .andExpect(header().exists("x-page-size"))
        // .andExpect(jsonPath("$", hasSize<Any>(pageSize)))
    }

    @Test
    fun `getBetsByMatchId test`(){
        val matchId = 1L    // предполагаемый идентификатор матча
        val pageNumber = 0  // предполагаемый номер страницы
        val pageSize = 5    // предполагаемый размер страницы

        mockMvc.perform(
            get("/matches/$matchId/bets")
                .param("pageNumber", pageNumber.toString())
                .param("pageSize", pageSize.toString())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items").value(pageSize))
    }

    @Test
    fun `updateMatch test`() {
        val matchId = 1L  // Предположим, что этот матч существует
        val updateMatchRequest = UpdateMatchRequest(name = "new name")

        mockMvc.perform(
            put("/matches/$matchId")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateMatchRequest))
        ).andExpect(status().isOk)
            .andExpect(jsonPath("$.matchId").value(matchId.toInt()))
            .andExpect(jsonPath("$.matchName").value("new name"))
    }

    @Test
    fun `endMatch test`() {
        val matchId = 1L            // Предположим, что этот матч существует
        val successfulBets = setOf(1L, 2L, 3L)   // Предполагаем, что эти ставки существуют

        mockMvc.perform(
            put("/matches/$matchId/end")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(successfulBets))
        )
            .andExpect(status().isNoContent)
    }


}
