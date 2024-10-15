package ru.itmo.vtbet.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.MatchDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SimpleAvailableBetsDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.entity.MatchesEntity
import ru.itmo.vtbet.model.entity.SportsEntity
import ru.itmo.vtbet.model.request.CreateMatchRequestDto
import ru.itmo.vtbet.model.request.CreateSportRequestDto
import ru.itmo.vtbet.model.request.UpdateMatchRequestDto
import ru.itmo.vtbet.repository.AvailableBetsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import kotlin.jvm.optionals.getOrElse
import kotlin.jvm.optionals.getOrNull

@Service
class ComplexMatchesService(
    private val matchesService: MatchesService,
    private val sportsService: SportsService,
    private val availableBetsService: AvailableBetsService,
//    private val availableBetsRepository: AvailableBetsRepository,
) {
    fun getMatches(pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesService.getMatches(PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result,
            total = result.size.toLong(),
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun getMatches(sportId: Long, pageNumber: Int, pageSize: Int): PagingDto<MatchDto> {
        val result = matchesService.getMatches(sportId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result,
            total = result.size.toLong(),
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
    fun delete(matchId: Long) = matchesService.delete(matchId)

    fun createMatch(createMatchRequestDto: CreateMatchRequestDto, sportId: Long): MatchDto {
        val sport = sportsService.getSport(sportId)
            ?: throw ResourceNotFoundException("Sport with id $sportId not found")
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
    fun endMatch(matchId: Long, successfulBets: Set<Long>) {
        val match = matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match with id $matchId not found")

        if (match.ended) {
            throw IllegalArgumentException("Match with ID: $matchId has already ended")
        }

        val allBetsForMatch = availableBetsService.getAllByMatchId(match.matchId)

        for (bet in allBetsForMatch) {
            if (successfulBets.contains(bet.availableBetId)) {
                val winnings = bet. * bet.ratio

                val userAccount = userAccountRepository.findById(bet.usersEntity.id!!)
                    .getOrElse { throw NoSuchElementException() }
                userAccount.balanceAmount += winnings
                userAccountRepository.save(userAccount)
            }
        }

        val matchToSave = match.copy(ended = true)
        matchesRepository.save(matchToSave)
    }

    @Transactional
    fun getBetsByMatchId(matchId: Long, pageNumber: Int, pageSize: Int): PagingDto<SimpleAvailableBetsDto> {
        val result = availableBetsRepository.findAllByMatchMatchId(matchId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map { it.toSimpleDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }
}