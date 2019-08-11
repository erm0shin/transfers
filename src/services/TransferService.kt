package ru.banking.services

import database.DatabaseFactory
import kotlinx.coroutines.runBlocking
import ru.banking.database.Wallet
import ru.banking.dto.OneWayPayment
import ru.banking.dto.TwoWayPayment
import ru.banking.repositories.WalletRepository

class TransferService(
    private val walletRepository: WalletRepository
) {
    suspend fun putMoney(payment: OneWayPayment): Wallet? {
        return DatabaseFactory.dbQuery {
            runBlocking {
                val wallet = walletRepository.getWallet(payment.walletId!!)
                wallet?.let {
                    val amount = payment.amount!!
                    if ((amount < 0L) && (it.ballance + amount < 0L))
                        throw RuntimeException("Balance cannot be negative")
                    it.ballance += amount
                    if (!walletRepository.updateWallet(wallet))
                        throw RuntimeException("Something went wrong. Please try again")
                }
                wallet
            }
        }
    }

    suspend fun transferMoney(twoWayPayment: TwoWayPayment): List<Wallet>? {
        return DatabaseFactory.dbQuery {
            runBlocking {
                val reduceMoneyRequest = OneWayPayment(twoWayPayment.fromWalletId, -twoWayPayment.amount!!)
                val fromWallet = putMoney(reduceMoneyRequest)
                val increaseMoneyRequest = OneWayPayment(twoWayPayment.toWalletId, twoWayPayment.amount)
                val toWallet = putMoney(increaseMoneyRequest)
                if (fromWallet == null || toWallet == null) {
                    null
                } else {
                    listOf(fromWallet, toWallet)
                }
            }
        }
    }
}