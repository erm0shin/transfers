package ru.banking.dto

import kotlinx.serialization.Serializable
import ru.banking.database.Citizenship
import ru.banking.database.Customer

@Serializable
class CustomerDTO(
    val id: Long?,
    val name: String,
    val age: Int,
    val citizenship: Citizenship,
    val wallets: List<WalletDTO>?
) {
    constructor(customer: Customer, wallets: List<WalletDTO> = ArrayList()) : this(
        customer.id.value,
        customer.name,
        customer.age,
        customer.citizenship,
        wallets
    )

    constructor(name: String, age: Int, citizenship: Citizenship) : this(
        null,
        name,
        age,
        citizenship,
        null
    )
}