package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SimpleTypeOfBetMatchDto
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.TypeOfBetMatchEntity
import ru.itmo.vtbet.model.request.UpdateMatchRequest
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.TypeOfBetMatchRepository
import ru.itmo.vtbet.repository.UserAccountRepository
import kotlin.jvm.optionals.getOrElse

@Service
class MatchesService(
    private val matchesRepository: MatchesRepository,
    private val betsRepository: BetsRepository,
    private val userAccountRepository: UserAccountRepository,
    private val typeOfBetMatchRepository: TypeOfBetMatchRepository,
) {
    fun getMatches(pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesRepository.findAll(PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(MatchesEntity::toDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun getBetsByMatchId(matchId: Long, pageNumber: Int, pageSize: Int): PagingDto<SimpleTypeOfBetMatchDto> {
        val result = typeOfBetMatchRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(TypeOfBetMatchEntity::toSimpleDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    @Transactional
    fun updateMatch(updateMatchRequest: UpdateMatchRequest, matchId: Long): MatchDto {
        val oldMatch = matchesRepository.findById(matchId).getOrElse {
            throw ResourceNotFoundException("Invalid id: $matchId")
        }
        val match = matchesRepository.save(
            MatchesEntity(
                matchId = matchId,
                matchName = updateMatchRequest.name ?: oldMatch.matchName,
                sport = oldMatch.sport,
                ended = oldMatch.ended,
            )
        )

        return match.toDto()
    }

    @Transactional
    fun endMatch(matchId: Long, successfulBets: Set<Long>) {
        val match = matchesRepository.findById(matchId)
            .orElseThrow { ResourceNotFoundException("No Match found with ID: $matchId") }

        if (match.ended) {
            throw IllegalArgumentException("Match with ID: $matchId has already ended")
        }

        val typeOfBets = typeOfBetMatchRepository.findAllByMatchMatchId(matchId)
        val allBetsForMatch = betsRepository.findByTypeOfBetMatchTypeOfBetMatchIdIn(typeOfBets.mapNotNull { it.typeOfBetMatchId })

        for (bet in allBetsForMatch) {
            if (successfulBets.contains(bet.id)) {
                val winnings = bet.amount * bet.ratio

                val userAccount = userAccountRepository.findById(bet.usersEntity.id!!)
                    .getOrElse { throw NoSuchElementException() }
                userAccount.balanceAmount += winnings
                userAccountRepository.save(userAccount)
            }
        }

        val matchToSave = match.copy(ended = true)
        matchesRepository.save(matchToSave)
    }

    fun delete(matchId: Long) {
        if (!matchesRepository.existsById(matchId)) {
            throw ResourceNotFoundException("No Match found with ID: $matchId")
        }
        matchesRepository.deleteById(matchId)
    }
}
