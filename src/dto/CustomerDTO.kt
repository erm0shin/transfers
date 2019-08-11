package ru.banking.dto

import ru.banking.database.Citizenship
import ru.banking.database.Customer

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

//    constructor(customer: Customer, wallets: List<WalletDTO>) : this(
//        customer.id.value,
//        customer.name,
//        customer.age,
//        customer.citizenship,
//        wallets
//    )
}