package routing

import io.ktor.application.Application
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import com.fasterxml.jackson.databind.*
import io.ktor.jackson.*
import io.ktor.features.*
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.sql.insert
import ru.banking.database.Citizenship
import ru.banking.database.Customer
import ru.banking.database.Customers
import ru.banking.dto.CustomerDTO
import ru.banking.repositories.CustomerRepository

fun Application.route(customerRepository: CustomerRepository) {
    routing {
        get("/") {
            var customer = Customer(EntityID(0L, Customers), "Vanya", 20, Citizenship.RUS)
            customerRepository.addCustomer(customer)
            customerRepository.addCustomer(customer)
            customerRepository.addCustomer(customer)
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        get("/customers") {
            //            call.respond(customerRepository.getAllCustomers())
            val customers = ArrayList<CustomerDTO>()
            for (customer in customerRepository.getAllCustomers())
                customers.add(CustomerDTO(customer))
            call.respond(customers)
        }

        get("/customers/{id}") {
            val customerId = call.parameters["id"]?.toLong()!!
            customerRepository.getCustomer(customerId)?.let { call.respond(it) }
        }
    }
}