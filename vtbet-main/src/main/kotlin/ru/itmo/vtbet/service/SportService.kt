package ru.itmo.vtbet.service

import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import ru.itmo.vtbet.model.dto.PagingDto
import ru.itmo.vtbet.model.dto.SportDto
import ru.itmo.vtbet.model.dto.toDto
import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.request.CreateSportRequest
import ru.itmo.vtbet.repository.SportRepository

@Service
class SportService(
    private val sportRepository: SportRepository,
) {

    fun getSports(pageNumber: Int, pageSize: Int): PagingDto<SportDto> {
        val result = sportRepository.findAll(PageRequest.of(pageNumber, pageSize))
        return PagingDto(
            items = result.content.map(SportEntity::toDto),
            total = result.totalElements,
            pageSize = pageSize,
            page = pageNumber,
        )
    }

    fun createSport(createSportRequest: CreateSportRequest): SportDto =
        sportRepository.save(SportEntity(sportName = createSportRequest.name)).toDto()
}