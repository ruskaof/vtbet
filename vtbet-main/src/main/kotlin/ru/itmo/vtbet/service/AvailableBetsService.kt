package ru.itmo.vtbet.service

import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import ru.itmo.vtbet.model.dto.AvailableBetDto
import ru.itmo.vtbet.model.dto.AvailableBetWithBetGroupDto
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.repository.AvailableBetsRepository

@Service
class AvailableBetsService(
    private val availableBetsRepository: AvailableBetsRepository
) {
    fun save(bet: AvailableBetWithBetGroupDto) =
        availableBetsRepository.saveAndFlush(
            bet.toEntity()
                .copy(availableBetId = null)
        ).toDtoWithBetGroup()

    fun update(bet: AvailableBetWithBetGroupDto) =
        availableBetsRepository.saveAndFlush(bet.toEntity()).toDto()

    fun getAllByMatchId(matchId: Long) =
        availableBetsRepository.findAllByMatchId(matchId).map { it.toDto() }

    fun getAvailableBet(availableBetId: Long) =
        availableBetsRepository.findByIdOrNull(availableBetId)?.toDto()

    fun getAvailableBetWithGroup(availableBetId: Long) =
        availableBetsRepository.findByIdOrNull(availableBetId)?.toDtoWithBetGroup()

    @Transactional
    fun getAllByMatchId(matchId: Long, pageNumber: Int, pageSize: Int): PagingDto<AvailableBetDto> {
        val result = availableBetsRepository.findAllByMatchId(matchId, PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map { it.toDto() },
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }
}