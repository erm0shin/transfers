package ru.banking.services

import database.DatabaseFactory
import kotlinx.coroutines.runBlocking
import ru.banking.database.Currency
import ru.banking.database.Wallet
import ru.banking.dto.OneWayPayment
import ru.banking.dto.TwoWayPayment
import ru.banking.repositories.WalletRepository
import ru.banking.utils.ExchangeRates

class TransferService(
    private val walletRepository: WalletRepository
) {
    private val exchangeRates = ExchangeRates()

    suspend fun putMoney(payment: OneWayPayment, amountCurrency: Currency? = null): Wallet? {
        return DatabaseFactory.dbQuery {
            runBlocking {
                val wallet = walletRepository.getWallet(payment.walletId!!)
                wallet?.let {
                    val paymentAmount = payment.amount!!
                    if ((paymentAmount < 0L) && (changeAmount(
                            it.ballance,
                            it.currency,
                            paymentAmount,
                            amountCurrency ?: it.currency
                        ) < 0.0)
                    ) throw RuntimeException("Balance cannot be negative")
                    it.ballance = changeAmount(it.ballance, it.currency, paymentAmount, amountCurrency ?: it.currency)
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
                val fromWallet = putMoney(reduceMoneyRequest, twoWayPayment.currency!!)
                val increaseMoneyRequest = OneWayPayment(twoWayPayment.toWalletId, twoWayPayment.amount)
                val toWallet = putMoney(increaseMoneyRequest, twoWayPayment.currency)
                if (fromWallet == null || toWallet == null) {
                    null
                } else {
                    listOf(fromWallet, toWallet)
                }
            }
        }
    }

    private fun changeAmount(
        ballance: Double, ballanceCurrency: Currency,
        amount: Double, amountCurrency: Currency
    ): Double {
        return if (ballanceCurrency == amountCurrency) {
            ballance + amount
        } else {
            ballance + exchangeRates.matrix[amountCurrency.ordinal, ballanceCurrency.ordinal]!! * amount
        }
    }
}