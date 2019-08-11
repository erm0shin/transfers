package ru.banking.dto

data class OneWayPayment(
    val walletId: Long?,
    val amount: Long?
)

data class TwoWayPayment(
    val fromWalletId: Long?,
    val toWalletId: Long?,
    val amount: Long?
)