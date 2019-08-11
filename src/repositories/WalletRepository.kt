package ru.banking.repositories

import database.DatabaseFactory
import database.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import ru.banking.database.Wallet
import ru.banking.database.Wallets

class WalletRepository {
    suspend fun getAllWallets(): List<Wallet> = dbQuery {
        Wallets.selectAll().map { toWallet(it) }
    }

    suspend fun getWallet(id: Long): Wallet? = dbQuery {
        Wallets.select {
            (Wallets.id eq id)
        }.mapNotNull { toWallet(it) }
            .singleOrNull()
    }

    suspend fun addWallet(wallet: Wallet): Wallet {
        var key = 0L
        dbQuery {
            key = (Wallets.insert {
                it[currency] = wallet.currency
                it[ballance] = wallet.ballance
                it[customerId] = wallet.customerId
            } get Wallets.id).value
        }
        return getWallet(key)!!
    }

    suspend fun addWallets(wallets: List<Wallet>) {
        dbQuery {
            Wallets.batchInsert(wallets) { wallet ->
                this[Wallets.currency] = wallet.currency
                this[Wallets.ballance] = wallet.ballance
                this[Wallets.customerId] = wallet.customerId
            }
        }
    }

    suspend fun deleteWallet(id: Long): Boolean {
        return dbQuery {
            Wallets.deleteWhere { Wallets.id eq id } > 0
        }
    }

    private fun toWallet(row: ResultRow): Wallet = Wallet(
        id = row[Wallets.id],
        currency = row[Wallets.currency],
        ballance = row[Wallets.ballance],
        customerId = row[Wallets.customerId]
    )
}