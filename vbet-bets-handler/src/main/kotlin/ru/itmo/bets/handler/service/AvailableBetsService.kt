package ru.itmo.bets.handler.service

import feign.FeignException
import jakarta.servlet.UnavailableException
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.bets.handler.client.SportsClient
import ru.itmo.bets.handler.repository.AvailableBetsRepository
import ru.itmo.bets.handler.model.dto.AvailableBetDto
import ru.itmo.bets.handler.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.ResourceNotFoundException

@Service
class AvailableBetsService(
    private val sportsClient: SportsClient,
    private val availableBetsRepository: AvailableBetsRepository,
) {
    @Transactional
    fun getAvailableBets(pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
        val result = availableBetsRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("availableBetId")))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    @Transactional
    fun getAvailableBet(availableBetId: Long): AvailableBetDto? =
        availableBetsRepository.findByIdOrNull(availableBetId)?.toDto()

    @Transactional
    fun getAvailableBetWithGroup(availableBetId: Long): AvailableBetWithBetGroupDto? =
        availableBetsRepository.findByIdOrNull(availableBetId)?.toDtoWithBetGroup()

    @Transactional
    fun delete(availableBetId: Long) {
        availableBetsRepository.deleteById(availableBetId)
    }

    @Transactional
    fun update(bet: AvailableBetWithBetGroupDto) =
        availableBetsRepository.saveAndFlush(bet.toEntity()).toDto()

    fun save(bet: AvailableBetWithBetGroupDto) =
        availableBetsRepository.saveAndFlush(
            bet.toEntity()
                .copy(availableBetId = null)
        ).toDtoWithBetGroup()

    fun getAllByMatchId(matchId: Long): List<AvailableBetDto> =
        availableBetsRepository.findAllByMatchId(matchId).map { it.toDto() }

    @Transactional
    fun getAllByMatchId(matchId: Long, pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
        try {
            sportsClient.getMatch(matchId)
        } catch (e: FeignException) {
            if (e.status() == 404) {
                throw ResourceNotFoundException("Match not found")
            } else {
                throw e
            }
        } catch (e: UnavailableException) {
            // ignore
        }

        val result = availableBetsRepository.findAllByMatchId(matchId, PageRequest.of(pageNumber, pageSize, Sort.by("availableBetId")))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }
}