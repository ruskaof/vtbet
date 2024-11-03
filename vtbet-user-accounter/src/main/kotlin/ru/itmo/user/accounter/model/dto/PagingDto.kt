package ru.itmo.user.accounter.model.dto

data class PagingDto<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val pageSize: Int,
)
