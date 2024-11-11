package ru.itmo.sports.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.itmo.common.dto.PagingDto
import ru.itmo.common.exception.ResourceNotFoundException
import ru.itmo.common.request.CreateSportRequestDto
import ru.itmo.common.request.UpdateSportRequestDto
import ru.itmo.common.dto.SportDto
import ru.itmo.common.entity.SportsEntity
import ru.itmo.sports.repository.SportsRepository


@Service
class SportsService(
    private val sportsRepository: SportsRepository,
) {
    @Transactional
    fun getSports(pageNumber: Int, pageSize: Int): PagingDto<SportDto> {
        val result = sportsRepository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("sportId")))
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

    @Transactional
    fun deleteSport(sportId: Long) {
        if (sportsRepository.existsById(sportId)) {
            sportsRepository.deleteById(sportId)
        } else {
            throw ResourceNotFoundException("Sport with id $sportId not found")
        }
    }

    fun updateSport(sportId: Long, updateMatchRequestDto: UpdateSportRequestDto): SportDto {
        val sport = sportsRepository.findByIdOrNull(sportId)
            ?: throw ResourceNotFoundException("Sport with id $sportId not found")

        return sportsRepository.save(sport.copy(sportName = updateMatchRequestDto.name)).toDto()
    }
}