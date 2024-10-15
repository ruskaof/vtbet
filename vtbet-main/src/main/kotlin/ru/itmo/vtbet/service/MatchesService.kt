package ru.itmo.vtbet.service

import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.repository.MatchesRepository

@Service
class MatchesService(
    private val matchesRepository: MatchesRepository,
) {
    fun getMatches(pageable: Pageable): List<MatchDto> {
        val result = matchesRepository.findAll(pageable)
        return result.content.map(MatchesEntity::toDto)
    }

    fun getMatches(sportId: Long, pageable: Pageable): List<MatchDto> {
        val result = matchesRepository.findAllBySportSportId(sportId, pageable)
        return result.content.map(MatchesEntity::toDto)
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