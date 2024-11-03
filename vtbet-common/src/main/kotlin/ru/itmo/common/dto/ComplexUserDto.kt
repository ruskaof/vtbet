package ru.itmo.common.dto

import java.math.BigDecimal
import java.time.Instant

data class ComplexUserDto(
    val userId: Long,
    val accountId: Long,
    val registrationDate: Instant,
    val balanceAmount: BigDecimal,
    val username: String,
    val email: String?,
    val phoneNumber: String?,
    val accountVerified: Boolean,
) {

    constructor(userDto: UserDto, userAccountDto: UserAccountDto) : this(
        userId = userDto.userId,
        accountId = userAccountDto.accountId,
        registrationDate = userDto.registrationDate,
        balanceAmount = userAccountDto.balanceAmount,
        username = userDto.username,
        email = userDto.email,
        phoneNumber = userDto.phoneNumber,
        accountVerified = userDto.accountVerified,
    )
}
