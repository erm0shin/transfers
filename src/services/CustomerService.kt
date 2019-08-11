package ru.banking.services

import database.DatabaseFactory
import javafx.application.Application.launch
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.dao.EntityID
import ru.banking.database.Citizenship
import ru.banking.database.Customer
import ru.banking.database.Customers
import ru.banking.database.Wallet
import ru.banking.dto.CustomerDTO
import ru.banking.dto.WalletDTO
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

    suspend fun getAllCustomers(): List<Customer> = customerRepository.getAllCustomers()

    suspend fun getCustomerWithWallets(customerId: Long): CustomerDTO? {
        val customers = customerRepository.getCustomerWithWallets(customerId)
        return if (customers?.size == 0) {
            null
        } else {
            val firstCustomer = customers?.get(0)!!
            CustomerDTO(firstCustomer, customers.mapNotNull { it.wallet }.map { WalletDTO(it) })
        }
    }

    suspend fun updateCustomer(customer: Customer): Boolean {
        return customerRepository.updateCustomer(customer)
    }

    suspend fun removeCustomer(customerId: Long): Boolean {
        return DatabaseFactory.dbQuery {
            runBlocking {
                val customer = getCustomerWithWallets(customerId)
                customer?.wallets?.forEach { wallet -> wallet.id?.let { walletRepository.deleteWallet(it) } }
                customerRepository.deleteCustomer(customerId)
            }
        }
    }

    suspend fun getWallet(walletId: Long): Wallet? {
        return walletRepository.getWallet(walletId)
    }

    suspend fun addWalletsToCustomer(wallets: List<Wallet>) {
        walletRepository.addWallets(wallets)
    }

    suspend fun removeWallet(walletId: Long): Boolean {
        return walletRepository.deleteWallet(walletId)
    }
}