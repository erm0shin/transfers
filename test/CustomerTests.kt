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
import org.junit.Test
import ru.banking.database.Citizenship
import ru.banking.dto.CustomerDTO
import kotlin.test.assertEquals

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
                        CustomerDTO(name = "Petr", age = 21, citizenship = Citizenship.RUS)
                    )
                )
            }
        }.apply {
            assertEquals(HttpStatusCode.OK, response.status())
            val customer = json.parse(CustomerDTO.serializer(), response.content!!)
            assertEquals(customer.id, 1)
            assertEquals(customer.name, "Petr")
            assertEquals(customer.age, 21)
            assertEquals(customer.citizenship, Citizenship.RUS)
        }
    }
}
