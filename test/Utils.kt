package ru.banking

import ru.banking.database.Citizenship
import ru.banking.database.Currency
import ru.banking.dto.CustomerDTO
import ru.banking.dto.WalletDTO

fun createCustomer(inputName: String = "Petr", inputId: Long? = null): CustomerDTO {
    return CustomerDTO(name = inputName, age = 21, citizenship = Citizenship.RUS, id = inputId)
}

fun createWallets(number: Int, customerId: Long): List<WalletDTO> {
    val result = ArrayList<WalletDTO>()
    for (i in 1..number) {
        result.add(
            WalletDTO(
                Currency.RUB,
                1000L,
                customerId
            )
        )
    }
    return result
}