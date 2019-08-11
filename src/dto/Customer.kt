package ru.banking.dto

import ru.banking.database.Citizenship
import ru.banking.database.Customer

class CustomerDTO(
    val name: String,
    val age: Int,
    val citizenship: Citizenship
) {
    constructor(customer: Customer) : this(
        customer.name,
        customer.age,
        customer.citizenship
    )
}