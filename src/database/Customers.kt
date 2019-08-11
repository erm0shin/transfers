package ru.banking.database

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.LongIdTable

object Customers : LongIdTable() {
    val name = varchar("name", 256)
    val age = integer("age")
    val citizenship = enumeration("citizenship", Citizenship::class)
}

class Customer(
    id: EntityID<Long>,
    val name: String,
    val age: Int,
    val citizenship: Citizenship,
    val wallet: Wallet? = null
) : LongEntity(id) {
    companion object : LongEntityClass<Customer>(Customers)
}

enum class Citizenship(val title: String) {
    RUS("Russian Federation"),
    USA("United States of America"),
    DEU("Germany")
}