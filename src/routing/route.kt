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
import ru.banking.database.*
import ru.banking.dto.CustomerDTO
import ru.banking.dto.WalletDTO
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

//        get("/customers/{id}") {
//            val customerId = call.parameters["id"]?.toLong()!!
//            customerService.getCustomer(customerId)?.let {
//                val customer = customerService.getCustomer(customerId)
//                if (customer != null) {
//                    call.respond(CustomerDTO(customer))
//                } else {
//                    call.response.status(HttpStatusCode.NoContent)
//                }
//            }
//        }

        get("/customers/{id}") {
            val customerId = call.parameters["id"]?.toLong()!!
            customerService.getCustomer(customerId)?.let {
                val customerDTO = customerService.getCustomerWithWallets(customerId)
                if (customerDTO != null) {
                    call.respond(customerDTO)
                } else {
                    call.response.status(HttpStatusCode.NoContent)
                }
            }
        }

        post("/wallets") {
            val walletsDTO = call.receive<Array<WalletDTO>>()
            val wallets = walletsDTO.map { Wallet(
                EntityID(0L, Wallets),
                it.currency,
                it.ballance,
                it.customerId
            ) }
            customerService.addWalletsToCustomer(wallets)
            call.response.status(HttpStatusCode.OK)
        }
    }
}