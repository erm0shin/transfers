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
import ru.banking.services.CustomerService

fun Application.route(customerService: CustomerService) {
    routing {
        get("/") {
            //            var customer = Customer(EntityID(0L, Customers), "Vanya", 20, Citizenship.RUS)
//            customerRepository.addCustomer(customer)
//            customerRepository.addCustomer(customer)
//            customerRepository.addCustomer(customer)
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/json/jackson") {
            call.respond(mapOf("hello" to "world"))
        }

        post("/customers") {
            val customerDTO = call.receive<CustomerDTO>()
            val customer = Customer(
                EntityID(0L, Customers),
                customerDTO.name,
                customerDTO.age,
                customerDTO.citizenship
            )
            call.respond(CustomerDTO(customerService.createCustomer(customer)))
        }

        get("/customers") {
            val customers = ArrayList<CustomerDTO>()
            for (customer in customerService.getAllCustomers())
                customers.add(CustomerDTO(customer))
            call.respond(customers)
        }

        get("/customers/{id}") {
            val customerId = call.parameters["id"]?.toLong()!!
            customerService.getCustomer(customerId)?.let {
                val customer = customerService.getCustomer(customerId)
                if (customer != null) {
                    call.respond(CustomerDTO(customer))
                } else {
                    call.response.status(HttpStatusCode.NoContent)
                }
            }
        }
    }
}