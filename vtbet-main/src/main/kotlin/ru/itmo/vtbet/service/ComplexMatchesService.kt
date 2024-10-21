package ru.itmo.vtbet.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.request.CreateMatchRequestDto
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto

@Service
class ComplexMatchesService(
    private val matchesService: MatchesService,
    private val sportsService: SportsService,
) {
    fun getMatches(pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesService.getMatches(PageRequest.of(pageNumber, pageSize, Sort.by("matchId")))
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

        val result = matchesService.getMatches(sportId, PageRequest.of(pageNumber, pageSize, Sort.by("matchId")))
        return PagingDto(
            items = result.content,
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    @Transactional
    fun updateMatch(updateMatchRequestDto: UpdateMatchRequestDto, matchId: Long): MatchDto {
        val oldMatch = matchesService.getMatch(matchId) ?: throw ResourceNotFoundException("Invalid id: $matchId")
        val match = matchesService.update(
            oldMatch.copy(
                name = updateMatchRequestDto.name ?: oldMatch.name,
            )
        )

        return match
    }

    @Transactional
    fun delete(matchId: Long) {
        matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match with id $matchId not found")
        matchesService.delete(matchId)
    }

    @Transactional
    fun createMatch(createMatchRequestDto: CreateMatchRequestDto): MatchDto {
        val sport = sportsService.getSport(createMatchRequestDto.sportId)
            ?: throw ResourceNotFoundException("Sport with id ${createMatchRequestDto.sportId} not found")
        val match = matchesService.save(
            MatchDto(
                matchId = 0,
                name = createMatchRequestDto.name,
                sport = sport,
                ended = false,
            )
        )

        return match
    }

    @Transactional
    fun endMatch(matchId: Long) {
        matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match with id $matchId not found")
        matchesService.endMatch(matchId)
    }
}