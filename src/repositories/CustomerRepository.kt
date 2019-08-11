package ru.banking.repositories

import database.DatabaseFactory
import database.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.banking.database.Customer
import ru.banking.database.Customers
import ru.banking.database.Wallet
import ru.banking.database.Wallets
import kotlin.and

class CustomerRepository {
    suspend fun getAllCustomers(): List<Customer> = dbQuery {
        Customers.selectAll().map { toCustomer(it) }
    }

    suspend fun getCustomer(id: Long): Customer? = dbQuery {
        Customers.select {
            (Customers.id eq id)
        }.mapNotNull { toCustomer(it) }
            .singleOrNull()
    }

    suspend fun getCustomerWithWallets(id: Long): List<Customer>? = dbQuery {
        (Customers leftJoin Wallets).slice(
            Customers.id, Customers.name, Customers.age, Customers.citizenship,
            Wallets.id, Wallets.currency, Wallets.ballance, Wallets.customerId
        ).select {
            (Customers.id eq id) and (Wallets.customerId eq Customers.id)
        }
//            .groupBy(Customers.id, Customers.name, Customers.age, Customers.citizenship)
            .map { toCustomerWithWallet(it) }
    }

    suspend fun addCustomer(customer: Customer): Customer {
        var key = 0L
        dbQuery {
            key = (Customers.insert {
                it[name] = customer.name
                it[age] = customer.age
                it[citizenship] = customer.citizenship
            } get Customers.id).value
        }
        return getCustomer(key)!!
    }

    suspend fun deleteCustomer(id: Long): Boolean {
        return dbQuery {
            Customers.deleteWhere { Customers.id eq id } > 0
        }
    }

    private fun toCustomer(row: ResultRow): Customer =
        Customer(
            id = row[Customers.id],
            name = row[Customers.name],
            age = row[Customers.age],
            citizenship = row[Customers.citizenship]
        )

    private fun toCustomerWithWallet(row: ResultRow): Customer =
        Customer(
            id = row[Customers.id],
            name = row[Customers.name],
            age = row[Customers.age],
            citizenship = row[Customers.citizenship],
            wallet = Wallet(row[Wallets.id], row[Wallets.currency], row[Wallets.ballance], row[Wallets.customerId])
        )
}