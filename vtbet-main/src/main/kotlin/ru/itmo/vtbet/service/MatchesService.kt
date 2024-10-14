package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.repository.BetsRepository
import ru.itmo.vtbet.repository.MatchesRepository
import ru.itmo.vtbet.repository.AvailableBetRepository
import ru.itmo.vtbet.repository.UserAccountRepository
import kotlin.jvm.optionals.getOrElse

@Service
class MatchesService(
    private val matchesRepository: MatchesRepository,
    private val betsRepository: BetsRepository,
    private val userAccountRepository: UserAccountRepository,
    private val typeOfBetMatchRepository: AvailableBetRepository,
) {



}
