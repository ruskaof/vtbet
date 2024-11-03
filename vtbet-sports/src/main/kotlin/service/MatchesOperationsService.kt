package ru.itmo.sports.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.sports.model.dto.MatchDto
import ru.itmo.sports.repository.MatchesRepository


@Service
class MatchesOperationsService(
    private val matchesRepository: MatchesRepository,
) {
    fun getMatches(pageable: Pageable): Page<MatchDto> {
        val result = matchesRepository.findAll(pageable)
        return result.map { it.toDto() }
    }

    fun getMatches(sportId: Long, pageable: Pageable): Page<MatchDto> {
        val result = matchesRepository.findAllBySportSportId(sportId, pageable)
        return result.map { it.toDto() }
    }

    @Transactional
    fun save(match: MatchDto) =
        matchesRepository.saveAndFlush(
            match
                .toEntity()
                .copy(matchId = null)
        ).toDto()

    @Transactional
    fun update(match: MatchDto) =
        matchesRepository.saveAndFlush(match.toEntity()).toDto()

    fun getMatch(matchId: Long): MatchDto? =
        matchesRepository.findByIdOrNull(matchId)?.toDto()

    @Transactional
    fun delete(matchId: Long) {
        matchesRepository.deleteById(matchId)
    }

    @Transactional
    fun endMatch(matchId: Long) {
        val match = matchesRepository.findByIdOrNull(matchId)
            ?: throw ResourceNotFoundException("Invalid id: $matchId")
        matchesRepository.save(match.copy(ended = true))
    }
}