package ru.itmo.vtbet.model.dto

import ru.itmo.vtbet.model.entity.SportEntity
import ru.itmo.vtbet.model.response.SportResponse

data class SportDto(
    val id: Long,
    val name: String,
)

fun SportEntity.toDto(): SportDto =
    SportDto(sportId!!, sportName)

fun SportDto.toResponse(): SportResponse =
    SportResponse(id, name)
