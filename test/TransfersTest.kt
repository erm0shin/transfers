package ru.banking

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.setBody
import io.ktor.server.testing.withTestApplication
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.list
import org.junit.Test
import ru.banking.database.Currency
import ru.banking.dto.CustomerDTO
import ru.banking.dto.OneWayPayment
import ru.banking.dto.TwoWayPayment
import ru.banking.dto.WalletDTO
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransfersTest {
    private val json = Json(JsonConfiguration.Stable)

    @Test
    fun test_one_way_payment() {
        // create customer
        var customerId: Long
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/customers") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        CustomerDTO.serializer(),
                        createCustomer()
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            customerId = customer.id!!
        }

        // create 3 wallets
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/wallets") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        WalletDTO.serializer().list,
                        createWallets(3, customerId)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // get customer with 3 wallets
        var walletId: Long
        var oldWalletBallance: Double
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            walletId = customer.wallets!![1].id!!
            oldWalletBallance = customer.wallets!![1].ballance
        }

        // do one-way request
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/payments/oneway") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        OneWayPayment.serializer(),
                        createOneWayPayment(walletId, 1000.0, Currency.RUB)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val wallet = json.parse(WalletDTO.serializer(), response.content!!)
            assertEquals(wallet.ballance, oldWalletBallance + 1000.0)
        }
    }

    @Test
    fun test_tow_way_payment() {
        // create customer
        var customerId: Long
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/customers") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        CustomerDTO.serializer(),
                        createCustomer()
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            customerId = customer.id!!
        }

        // create 3 wallets
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/wallets") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        WalletDTO.serializer().list,
                        createWallets(3, customerId)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // get customer with 3 wallets
        var fromWalletId: Long
        var oldFromWalletBallance: Double
        var toWalletId: Long
        var oldToWalletBallance: Double
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            fromWalletId = customer.wallets!![1].id!!
            oldFromWalletBallance = customer.wallets!![1].ballance
            toWalletId = customer.wallets!![2].id!!
            oldToWalletBallance = customer.wallets!![2].ballance
        }

        // do two-way request
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/payments/twoway") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        TwoWayPayment.serializer(),
                        createTowWayPayment(fromWalletId, toWalletId, 500.0, Currency.RUB)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val wallets = json.parse(WalletDTO.serializer().list, response.content!!)
            assertEquals(wallets.find { it.id == fromWalletId }!!.ballance, oldFromWalletBallance - 500.0)
            assertEquals(wallets.find { it.id == toWalletId }!!.ballance, oldToWalletBallance + 500.0)
        }
    }

    @Test
    fun test_tow_way_multicurrency_payment() {
        // create customer
        var customerId: Long
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/customers") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        CustomerDTO.serializer(),
                        createCustomer()
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            customerId = customer.id!!
        }

        // create 3 wallets
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/wallets") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        WalletDTO.serializer().list,
                        createWallets(3, customerId)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // get customer with 3 wallets
        var fromWalletId: Long
        var oldFromWalletBallance: Double
        var toWalletId: Long
        var oldToWalletBallance: Double
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            fromWalletId = customer.wallets!![1].id!!
            oldFromWalletBallance = customer.wallets!![1].ballance
            toWalletId = customer.wallets!![2].id!!
            oldToWalletBallance = customer.wallets!![2].ballance
        }

        // do two-way request
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/payments/twoway") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        TwoWayPayment.serializer(),
                        createTowWayPayment(fromWalletId, toWalletId, 10.0, Currency.USD)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val wallets = json.parse(WalletDTO.serializer().list, response.content!!)
            val fromWallet = wallets.find { it.id == fromWalletId }!!
            assertTrue { fromWallet.ballance > (oldFromWalletBallance - 10 * 65.3) }
            assertTrue { fromWallet.ballance < (oldFromWalletBallance - 10 * 65.2) }
            val toWallet = wallets.find { it.id == toWalletId }!!
            assertTrue { toWallet.ballance > (oldToWalletBallance + 10 * 65.2) }
            assertTrue { toWallet.ballance < (oldToWalletBallance + 10 * 65.3) }
        }
    }

    @Test
    fun test_bad_requests() {
        // create customer
        var customerId: Long
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/customers") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        CustomerDTO.serializer(),
                        createCustomer()
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            customerId = customer.id!!
        }

        // create 3 wallets
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/wallets") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        WalletDTO.serializer().list,
                        createWallets(3, customerId)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // get customer with 3 wallets
        var fromWalletId: Long
        var toWalletId: Long
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            fromWalletId = customer.wallets!![1].id!!
            toWalletId = customer.wallets!![2].id!!
        }

        // do two-way request with negative resulting ballance
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/payments/twoway") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        TwoWayPayment.serializer(),
                        createTowWayPayment(fromWalletId, toWalletId, 100.0, Currency.USD)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.InternalServerError, response.status())
        }

        // do one-way request with negative resulting ballance
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/payments/oneway") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        OneWayPayment.serializer(),
                        createOneWayPayment(toWalletId, -1000.0, Currency.EUR)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.InternalServerError, response.status())
        }

        // do one-way request with absent wallet
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/payments/oneway") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        OneWayPayment.serializer(),
                        createOneWayPayment(1000L, 1000.0, Currency.RUB)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }

        // assert that the ballances remain the same
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            assertEquals(customer.wallets!![0].ballance, 1000.0)
            assertEquals(customer.wallets!![1].ballance, 1000.0)
            assertEquals(customer.wallets!![2].ballance, 1000.0)
        }
    }
}