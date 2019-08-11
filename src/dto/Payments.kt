package ru.banking.dto

import kotlinx.serialization.Serializable
import ru.banking.database.Currency

@Serializable
data class OneWayPayment(
    val walletId: Long?,
    val amount: Double?,
    val currency: Currency?
)

@Serializable
data class TwoWayPayment(
    val fromWalletId: Long?,
    val toWalletId: Long?,
    val amount: Double?,
    val currency: Currency?
)