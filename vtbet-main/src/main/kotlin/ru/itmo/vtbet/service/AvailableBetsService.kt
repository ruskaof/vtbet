package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.vtbet.exception.ResourceNotFoundException
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.repository.AvailableBetsRepository

@Service
class AvailableBetsService(
    private val availableBetsRepository: AvailableBetsRepository,
    private val matchesOperationsService: MatchesOperationsService,
) {
    fun save(bet: AvailableBetWithBetGroupDto) =
        availableBetsRepository.saveAndFlush(
            bet.toEntity()
                .copy(availableBetId = null)
        ).toDtoWithBetGroup()

    fun update(bet: AvailableBetWithBetGroupDto) =
        availableBetsRepository.saveAndFlush(bet.toEntity()).toDto()

    fun getAllByMatchId(matchId: Long): List<AvailableBetDto> =
        availableBetsRepository.findAllByMatchId(matchId).map { it.toDto() }

    fun getAvailableBet(availableBetId: Long): AvailableBetDto? =
        availableBetsRepository.findByIdOrNull(availableBetId)?.toDto()

    fun getAvailableBetWithGroup(availableBetId: Long): AvailableBetWithBetGroupDto? =
        availableBetsRepository.findByIdOrNull(availableBetId)?.toDtoWithBetGroup()

    @Transactional
    fun getAllByMatchId(matchId: Long, pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
        matchesOperationsService.getMatch(matchId)
            ?: throw ResourceNotFoundException("Match with id $matchId not found")

        val result = availableBetsRepository.findAllByMatchId(matchId, PageRequest.of(pageNumber, pageSize, Sort.by("availableBetId")))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun getAvailableBets(pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
        val result = availableBetsRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("availableBetId")))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun delete(availableBetId: Long) {
        availableBetsRepository.deleteById(availableBetId)
    }
}