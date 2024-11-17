package ru.itmo.sports.controller

import org.springframework.http.HttpHeaders

const val X_TOTAL_COUNT = "X-Total-Count"
const val X_CURRENT_PAGE = "X-Current-Page"
const val X_PAGE_SIZE = "X-Page-Size"

fun preparePagingHeaders(
    totalCount: Long,
    currentPage: Int,
    pageSize: Int
) = HttpHeaders().apply {
    put(X_TOTAL_COUNT, listOf(totalCount.toString()))
    put(X_CURRENT_PAGE, listOf(currentPage.toString()))
    put(X_PAGE_SIZE, listOf(pageSize.toString()))
}
