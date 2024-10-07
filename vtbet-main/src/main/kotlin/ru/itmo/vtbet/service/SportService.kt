package ru.itmo.vtbet.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SimpleTypeOfBetMatchDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.request.CreateMatchRequest
import ru.itmo.vtbet.model.request.CreateSportRequest
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import kotlin.jvm.optionals.getOrNull

@Service
class SportService(
    private val sportRepository: SportRepository,
    private val matchesRepository: MatchesRepository,
    private val typeOfBetMatchRepository: TypeOfBetMatchRepository,
) {
    fun getSports(pageNumber: Int, pageSize: Int): PagingDto<SportDto> {
        val result = sportRepository.findAll(PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(SportEntity::toDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun createSport(createSportRequest: CreateSportRequest): SportDto =
        sportRepository.save(SportEntity(sportName = createSportRequest.name)).toDto()

    fun getMatches(sportId: Long, pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesRepository.findAllBySportSportId(sportId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(MatchesEntity::toDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun createMatch(createMatchRequest: CreateMatchRequest, sportId: Long): MatchDto {
        val sport: SportEntity = sportRepository.findById(sportId).getOrNull()
            ?: throw ResourceNotFoundException("Sport with id $sportId not found")
        val match = matchesRepository.save(
            MatchesEntity(
                matchName = createMatchRequest.name,
                sport = sport,
                ended = false
            )
        )

        return match.toDto()
    }

    fun getTypeOfBetMatch(matchId: Long, pageNumber: Int, pageSize: Int): PagingDto<SimpleTypeOfBetMatchDto> {
        val result = typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(TypeOfBetMatchEntity::toSimpleDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }
}
