package ru.banking

import com.fasterxml.jackson.databind.SerializationFeature
import controllers.customerController
import controllers.transferController
import database.DatabaseFactory
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import ru.banking.repositories.CustomerRepository
import ru.banking.repositories.WalletRepository
import ru.banking.services.CustomerService
import ru.banking.services.TransferService

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    DatabaseFactory.init()
    val customerRepository = CustomerRepository()
    val walletRepository = WalletRepository()
    val customerService = CustomerService(customerRepository, walletRepository)
    val transferService = TransferService(walletRepository)

    customerController(customerService)
    transferController(transferService)
}

