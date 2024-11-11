package ru.itmo.common.dto

import java.math.BigDecimal
import java.time.Instant

data class ComplexUserDto(
    val userId: Long,
    val registrationDate: Instant,
    val balanceAmount: BigDecimal,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
    val accountVerified: Boolean,
) {

    constructor(userDto: UserDto, userAccountDto: UserAccountDto) : this(
        userId = userDto.userId,
        registrationDate = userAccountDto.registrationDate,
        balanceAmount = userAccountDto.balanceAmount,
        username = userDto.username,
        email = userAccountDto.email,
        phoneNumber = userAccountDto.phoneNumber,
        accountVerified = userAccountDto.accountVerified,
    )
}
