package ru.itmo.vtbet.service

import org.springframework.stereotype.Service
import ru.itmo.vtbet.repository.*

@Service
class BetService(
    private val typeOfBetMatchRepository: TypeOfBetMatchRepository,
    private val matchesRepository: MatchesRepository,
    private val sportRepository: SportRepository,
    private val typeOfBetRepository: TypeOfBetRepository,
    private val betGroupRepository: BetGroupRepository,
    private val betsRepository: BetsRepository,
) {
}