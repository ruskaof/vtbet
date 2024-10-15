package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.vtbet.model.dto.BetGroupDto
import ru.itmo.vtbet.model.dto.FullAvailableBetWithBetGroupDto
import ru.itmo.vtbet.model.request.CreateAvailableBetRequestDto
import ru.itmo.vtbet.model.request.UpdateAvailableBetRequestDto

/**
 * Тут логика работы с созданием ставок
 * Например, bet group - это может быть (победа 1 команды, победа 2 команды, ничья)
 * или (больше 3 голов, меньше или равно 3 голов) и тд.
 */
//FIXME: а еще по хорошему все простые сервисы заменить на интерфейсы
@Service
class AdminBetService(
    private val matchesService: MatchesService,
    private val betsService: BetsService,
    private val availableBetsService: AvailableBetsService,
) {
    @Transactional
    fun updateAvailableBet(betId: Long, updateAvailableBetRequestDto: UpdateAvailableBetRequestDto): AvailableBetDto {
        val bet = availableBetsService.getAvailableBetWithGroup(betId)
            ?: throw ResourceNotFoundException("Bet not found")
        return availableBetsService.update(
            bet.copy(ratio = updateAvailableBetRequestDto.ratio.toBigDecimal())
        )
    }

    @Transactional
    fun createAvailableBet(matchId: Long, request: CreateAvailableBetRequestDto): FullAvailableBetWithBetGroupDto {
        val group = betsService.getBetGroup(request.groupId)
            ?: throw ResourceNotFoundException("Bet group not found")
        val match = matchesService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match not found")
        val bet = availableBetsService.save(
            AvailableBetWithBetGroupDto(
                availableBetId = 0,
                ratio = request.ratio,
                betsClosed = false,
                matchId = matchId,
                betGroupDto = BetGroupDto(
                    groupId = group.groupId,
                    description = group.description,
                ),
            )
        )
        return FullAvailableBetWithBetGroupDto(
            availableBetId = bet.availableBetId,
            ratio = bet.ratio,
            betsClosed = bet.betsClosed,
            betGroupDto = bet.betGroupDto,
            match = match,
        )
    }
}
