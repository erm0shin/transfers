package ru.banking.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

object Wallets : LongIdTable() {
    val currency = enumeration("currency", Currency::class)
    val ballance = long("ballance")
    val customerId = long("customerId")
}

class Wallet(
    id: EntityID<Long>,
    val currency: Currency,
    val ballance: Long,
    val customerId: Long
) : LongEntity(id) {
    companion object : LongEntityClass<Wallet>(Wallets)
}

enum class Currency(val title: String) {
    RUB("Russian rouble"),
    USD("American dollar"),
    EUR("Euro")
}