package ru.banking.repositories

import database.DatabaseFactory
import database.DatabaseFactory.dbQuery
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import ru.banking.database.Customer
import ru.banking.database.Customers

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

    private fun toCustomer(row: ResultRow): Customer =
        Customer(
            id = row[Customers.id],
            name = row[Customers.name],
            age = row[Customers.age],
            citizenship = row[Customers.citizenship]
        )
}


//import ru.banking.entities.Customer
//
//class CustomerRepository {
//    private var customers = mutableListOf<Customer>()
//
//    public fun addCustomer(customer: Customer) {
//        customers.add(customer)
//    }
//
//    public fun removeCustomer(customer: Customer) {
//        customers.remove(customer)
//    }
//
//    public fun findCustomer(customerId: Long): Customer? {
//        return customers.find { it.id == customerId }
//    }
//
//    public fun updateCustomer(customer: Customer) {
//        for ((index, value) in customers.withIndex()) {
//            if (value.id == customer.id) customers[index] = customer
//        }
//    }
//}