package ru.banking.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

object Wallets : LongIdTable() {
    val currency = enumeration("citizenship", Currency::class)
    val ballance = long("ballance")
    val customer = reference("customer", Customers)
}

class Wallet(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<Wallet>(Wallets)

    var currency by Wallets.currency
    var ballance by Wallets.ballance
    var customer by Customer referencedOn Wallets.customer
}

enum class Currency(val title: String) {
    RUB("Russian rouble"),
    USD("American dollar"),
    EUR("Euro")
}