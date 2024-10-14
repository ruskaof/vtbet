package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.request.CreateMatchRequestDto
import ru.itmo.vtbet.model.request.CreateSportRequestDto
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.SportRepository
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Service
class MatchService(
    private val matchesRepository: MatchesRepository,
    private val sportRepository: SportRepository,
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

    fun getMatches(pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesRepository.findAll(PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(MatchesEntity::toDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun getMatches(sportId: Long, pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesRepository.findAllBySportSportId(sportId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(MatchesEntity::toDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    @Transactional
    fun updateMatch(updateMatchRequestDto: UpdateMatchRequestDto, matchId: Long): MatchDto {
        val oldMatch = matchesRepository.findById(matchId).getOrElse {
            throw ResourceNotFoundException("Invalid id: $matchId")
        }
        val match = matchesRepository.save(
            MatchesEntity(
                matchId = matchId,
                matchName = updateMatchRequestDto.name ?: oldMatch.matchName,
                sport = oldMatch.sport,
                ended = oldMatch.ended,
            )
        )

        return match.toDto()
    }

    fun createSport(createSportRequestDto: CreateSportRequestDto): SportDto =
        sportRepository.save(SportEntity(sportName = createSportRequestDto.name)).toDto()

    fun createMatch(createMatchRequestDto: CreateMatchRequestDto, sportId: Long): MatchDto {
        val sport: SportEntity = sportRepository.findById(sportId).getOrNull()
            ?: throw ResourceNotFoundException("Sport with id $sportId not found")
        val match = matchesRepository.save(
            MatchesEntity(
                matchName = createMatchRequestDto.name,
                sport = sport,
                ended = false
            )
        )

        return match.toDto()
    }

    fun getMatch(matchId: Long): MatchDto? =
        matchesRepository.findById(matchId).getOrNull()?.toDto()

    @Transactional
    fun endMatch(matchId: Long) {
        val match = matchesRepository.findById(matchId).getOrNull()
            ?: throw ResourceNotFoundException("Invalid id: $matchId")
        matchesRepository.save(match.copy(ended = true))
    }
}