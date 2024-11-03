package ru.itmo.sports.ru.itmo.sports.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.CreateMatchRequestDto
import ru.itmo.common.request.UpdateMatchRequestDto
import ru.itmo.sports.ru.itmo.sports.model.dto.MatchDto


@Service
class ComplexMatchesService(
    private val matchesOperationsService: MatchesOperationsService,
    private val sportsService: SportsService,
) {
    fun getMatches(pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesOperationsService.getMatches(PageRequest.of(pageNumber, pageSize, Sort.by("matchId")))
        return PagingDto(
            items = result.content,
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun getMatches(sportId: Long, pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        sportsService.getSport(sportId)
            ?: throw ResourceNotFoundException("Sport with id $sportId not found")

        val result = matchesOperationsService.getMatches(sportId, PageRequest.of(pageNumber, pageSize, Sort.by("matchId")))
        return PagingDto(
            items = result.content,
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    @Transactional
    fun updateMatch(updateMatchRequestDto: UpdateMatchRequestDto, matchId: Long): MatchDto {
        val oldMatch = matchesOperationsService.getMatch(matchId) ?: throw ResourceNotFoundException("Invalid id: $matchId")
        val match = matchesOperationsService.update(
            oldMatch.copy(
                name = updateMatchRequestDto.name ?: oldMatch.name,
            )
        )

        return match
    }

    @Transactional
    fun delete(matchId: Long) {
        matchesOperationsService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match with id $matchId not found")
        matchesOperationsService.delete(matchId)
    }

    @Transactional
    fun createMatch(createMatchRequestDto: CreateMatchRequestDto): MatchDto {
        val sport = sportsService.getSport(createMatchRequestDto.sportId)
            ?: throw ResourceNotFoundException("Sport with id ${createMatchRequestDto.sportId} not found")
        val match = matchesOperationsService.save(
            MatchDto(
                matchId = 0,
                name = createMatchRequestDto.name,
                sport = sport,
                ended = false,
            )
        )

        return match
    }
}