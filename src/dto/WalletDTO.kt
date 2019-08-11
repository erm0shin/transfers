package ru.banking.dto

import ru.banking.database.Citizenship
import ru.banking.database.Currency
import ru.banking.database.Customer
import ru.banking.database.Wallet

class WalletDTO(
    val id: Long,
    val currency: Currency,
    val ballance: Long,
    val customerId: Long
) {
    constructor(wallet: Wallet) : this(
        wallet.id.value,
        wallet.currency,
        wallet.ballance,
        wallet.customerId
    )
}