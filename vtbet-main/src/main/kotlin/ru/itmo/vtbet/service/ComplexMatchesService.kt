package ru.itmo.vtbet.service

import org.springframework.data.domain.PageRequest
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
    private val betsService: BetsService,
    private val availableBetsService: AvailableBetsService,
    private val usersAccountsService: UsersAccountsService,
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

        val allAvailableBetsForMatch = availableBetsService.getAllByMatchId(match.matchId)
        val allAvailableBetIds = allAvailableBetsForMatch.map { it.availableBetId }
        val allBetsForMatch = betsService.getBetsByAvailableBetIds(allAvailableBetIds)

        for (bet in allBetsForMatch) {
            if (successfulBets.contains(bet.availableBetId)) {
                val winnings = bet.amount * bet.ratio

                val userAccount = usersAccountsService.getComplexUserAccount(bet.userId)
                userAccount?.let {
                    usersAccountsService.save(
                        it.copy(balanceAmount = it.balanceAmount + winnings)
                    )
                }
            }
        }

        val matchToSave = match.copy(ended = true)
        matchesService.save(matchToSave)
    }
}