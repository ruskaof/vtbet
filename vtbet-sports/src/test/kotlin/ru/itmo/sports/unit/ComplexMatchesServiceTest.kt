package ru.itmo.sports.unit

import org.mockito.Mockito
import ru.itmo.sports.ru.itmo.sports.service.ComplexMatchesService
import ru.itmo.sports.ru.itmo.sports.service.MatchesOperationsService
import ru.itmo.sports.ru.itmo.sports.service.SportsService

class ComplexMatchesServiceTest {

    private val matchesOperationsService = Mockito.mock(MatchesOperationsService::class.java)

    private val sportsService = Mockito.mock(SportsService::class.java)

    private val complexMatchesService =
        ComplexMatchesService(matchesOperationsService, sportsService)


}