package ru.itmo.common.utils

import java.math.BigDecimal
import java.math.RoundingMode

const val MAX_PAGE_SIZE = 50L
const val DECIMAL_SCALE = 2

fun BigDecimal.scaled() = setScale(DECIMAL_SCALE, RoundingMode.FLOOR)
