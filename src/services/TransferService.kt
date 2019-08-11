package ru.banking.services

import ru.banking.repositories.CustomerRepository
import ru.banking.repositories.WalletRepository

class TransferService(
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository
) {

}