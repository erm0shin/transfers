package controllers

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.routing.routing
import ru.banking.dto.OneWayPayment
import ru.banking.dto.TwoWayPayment
import ru.banking.dto.WalletDTO
import ru.banking.services.TransferService

fun Application.transferController(transferService: TransferService) {
    routing {
        route("/payments") {
            // create one-way payment (only with one wallet)
            post("/oneway") {
                val payment = call.receive<OneWayPayment>()
                if (payment.walletId == null || payment.amount == null || payment.currency == null)
                    call.response.status(HttpStatusCode.BadRequest)
                try {
                    val wallet = transferService.putMoney(payment)
                    if (wallet != null) {
                        call.respond(WalletDTO(wallet))
                    } else {
                        call.response.status(HttpStatusCode.BadRequest)
                    }
                } catch (e: Exception) {
                    call.respondText(e.message ?: "", contentType = ContentType.Text.Plain)
                    call.response.status(HttpStatusCode.InternalServerError)
                }
            }

            // create two-way payment (with two wallets)
            post("/twoway") {
                val payment = call.receive<TwoWayPayment>()
                if (payment.fromWalletId == null || payment.toWalletId == null ||
                    payment.amount == null || payment.currency == null
                )
                    call.response.status(HttpStatusCode.BadRequest)
                try {
                    val wallets = transferService.transferMoney(payment)
                    if (wallets != null) {
                        val result = ArrayList<WalletDTO>()
                        for (wallet in wallets)
                            result.add(WalletDTO(wallet))
                        call.respond(result)
                    } else {
                        call.response.status(HttpStatusCode.BadRequest)
                    }
                } catch (e: Exception) {
                    call.respondText(e.message ?: "", contentType = ContentType.Text.Plain)
                    call.response.status(HttpStatusCode.InternalServerError)
                }
            }
        }
    }
}