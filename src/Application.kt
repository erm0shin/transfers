package ru.banking

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import database.DatabaseFactory
import io.ktor.jackson.*
import io.ktor.features.*
import routing.*
import ru.banking.repositories.CustomerRepository
import ru.banking.repositories.WalletRepository
import ru.banking.services.CustomerService

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

    customerRouter(customerService)
}

