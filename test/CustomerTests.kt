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
import ru.banking.database.Citizenship
import ru.banking.database.Currency
import ru.banking.dto.CustomerDTO
import ru.banking.dto.WalletDTO
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CustomerTests {

    private val json = Json(JsonConfiguration.Stable)

    @Test
    fun test_customer_creation() {
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
            assertNotNull(customer.id)
            assertEquals(customer.name, "Petr")
            assertEquals(customer.age, 21)
            assertEquals(customer.citizenship, Citizenship.RUS)
        }
    }

    @Test
    fun test_customer_with_wallets_creation() {
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
            assertNotNull(customer.id)
            customerId = customer.id!!
            assertEquals(customer.name, "Petr")
            assertEquals(customer.age, 21)
            assertEquals(customer.citizenship, Citizenship.RUS)
        }

        // create wallets
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

        // get customer with wallets
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            assertEquals(customer.name, "Petr")
            assertEquals(customer.age, 21)
            assertEquals(customer.citizenship, Citizenship.RUS)
            assertEquals(customer.wallets!!.size, 3)
            assertEquals(customer.wallets!![0].currency, Currency.RUB)
            assertEquals(customer.wallets!![1].ballance, 1000.0)
            assertEquals(customer.wallets!![2].customerId, customerId)
        }
    }

    @Test
    fun test_entities_deletion() {
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

        // create wallets
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
        var firstWalletId: Long
        val secondWalletId: Long
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            firstWalletId = customer.wallets!![1].id!!
            secondWalletId = customer.wallets!![2].id!!
        }

        // delete one wallet
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/wallets/$firstWalletId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // get customer with 2 wallets
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            assertEquals(customer.wallets!!.size, 2)
            assertNull(customer.wallets!!.find { it.id == firstWalletId })
        }

        // delete customer
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // get no customer
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }

        // get no wallet
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/wallets/$secondWalletId")
        }.apply {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }
    }

    @Test
    fun test_customer_updating() {
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
            assertNotNull(customer.id)
            customerId = customer.id!!
            assertEquals(customer.name, "Petr")
            assertEquals(customer.age, 21)
            assertEquals(customer.citizenship, Citizenship.RUS)
        }

        // update customer
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/customers") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        CustomerDTO.serializer(),
                        createCustomer("Vanya", customerId)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
        }

        // get customer with new data
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/$customerId")
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            assertEquals(customer.name, "Vanya")
            assertEquals(customer.age, 21)
            assertEquals(customer.citizenship, Citizenship.RUS)
        }
    }

    @Test
    fun test_bad_requests() {
        // get absent customer
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/customers/1000")
        }.apply {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }

        // update absent customer
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Post, "/customers") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        CustomerDTO.serializer(),
                        createCustomer("Vanya", 2000L)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }

        // delete absent customer
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/customers/3000")
        }.apply {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }

        // create wallets by absent customer
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Put, "/wallets") {
                addHeader(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                setBody(
                    json.stringify(
                        WalletDTO.serializer().list,
                        createWallets(3, 1000L)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, response.status())
        }

        // delete absent wallet
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Delete, "/wallets/4000")
        }.apply {
            assertEquals(HttpStatusCode.NoContent, response.status())
        }
    }

}
