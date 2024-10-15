package ru.itmo.vtbet.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.entity.SportsEntity
import ru.itmo.vtbet.model.request.CreateSportRequestDto
import ru.itmo.vtbet.repository.SportsRepository

@Service
class SportsService(
    private val sportsRepository: SportsRepository,
) {
    @Transactional
    fun getSports(pageNumber: Int, pageSize: Int): PagingDto<SportDto> {
        val result = sportsRepository.findAll(PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(SportsEntity::toDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun getSport(sportId: Long): SportDto? =
        sportsRepository.findByIdOrNull(sportId)?.toDto()

    @Transactional
    fun createSport(createSportRequestDto: CreateSportRequestDto): SportDto =
        sportsRepository.save(SportsEntity(sportName = createSportRequestDto.name)).toDto()
}