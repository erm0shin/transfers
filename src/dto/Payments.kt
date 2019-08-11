package ru.banking.dto

import ru.banking.database.Currency

data class OneWayPayment(
    val walletId: Long?,
    val amount: Double?
)

data class TwoWayPayment(
    val fromWalletId: Long?,
    val toWalletId: Long?,
    val amount: Double?,
    val currency: Currency?
)