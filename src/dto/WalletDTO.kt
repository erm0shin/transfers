package ru.banking.dto

import kotlinx.serialization.Serializable
import ru.banking.database.Currency
import ru.banking.database.Wallet

@Serializable
class WalletDTO(
    val id: Long?,
    val currency: Currency,
    var ballance: Long = 0L,
    val customerId: Long
) {
    constructor(wallet: Wallet) : this(
        wallet.id.value,
        wallet.currency,
        wallet.ballance,
        wallet.customerId
    )

    constructor(currency: Currency, ballance: Long, customerId: Long, id: Long? = null) : this(
        id,
        currency,
        ballance,
        customerId
    )
}