package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import ru.itmo.vtbet.repository.AvailableBetsRepository

@Service
class AvailableBetsService(
    private val availableBetsRepository: AvailableBetsRepository
) {
    fun getAllByMatchId(matchId: Long) =
        availableBetsRepository.findAllByMatchMatchId(matchId).map { it.toDto() }

}