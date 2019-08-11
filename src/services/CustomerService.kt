package ru.banking.services

import org.jetbrains.exposed.dao.EntityID
import ru.banking.database.Citizenship
import ru.banking.database.Customer
import ru.banking.database.Customers
import ru.banking.database.Wallet
import ru.banking.dto.CustomerDTO
import ru.banking.repositories.CustomerRepository
import ru.banking.repositories.WalletRepository

class CustomerService(
    private val customerRepository: CustomerRepository,
    private val walletRepository: WalletRepository
) {
    suspend fun createCustomer(customer: Customer): Customer {
        return customerRepository.addCustomer(customer)
    }

    suspend fun getCustomer(customerId: Long): Customer? {
        return customerRepository.getCustomer(customerId)
    }

    suspend fun addWalletsToCustomer(wallets: List<Wallet>) {
        walletRepository.addWallets(wallets)
    }

    suspend fun getAllCustomers(): List<Customer> = customerRepository.getAllCustomers()
}