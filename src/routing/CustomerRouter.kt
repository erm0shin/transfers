package routing

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.delete
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.jetbrains.exposed.dao.EntityID
import ru.banking.database.Customer
import ru.banking.database.Customers
import ru.banking.database.Wallet
import ru.banking.database.Wallets
import ru.banking.dto.CustomerDTO
import ru.banking.dto.WalletDTO
import ru.banking.services.CustomerService

fun Application.customerRouter(customerService: CustomerService) {
    routing {

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
            val customerDTO = customerService.getCustomerWithWallets(customerId)
            if (customerDTO != null) {
                call.respond(customerDTO)
            } else {
                call.response.status(HttpStatusCode.NoContent)
            }
        }

        delete("/customers/{id}") {
            val customerId = call.parameters["id"]?.toLong()!!
            if (customerService.removeCustomer(customerId)) {
                call.response.status(HttpStatusCode.OK)
            } else {
                call.response.status(HttpStatusCode.NoContent)
            }
        }

        post("/wallets") {
            val walletsDTO = call.receive<Array<WalletDTO>>()
            val wallets = walletsDTO.map {
                Wallet(
                    EntityID(0L, Wallets),
                    it.currency,
                    it.ballance,
                    it.customerId
                )
            }
            customerService.addWalletsToCustomer(wallets)
            call.response.status(HttpStatusCode.OK)
        }

        delete("/wallets/{id}") {
            val walletId = call.parameters["id"]?.toLong()!!
            if (customerService.removeWallet(walletId)) {
                call.response.status(HttpStatusCode.OK)
            } else {
                call.response.status(HttpStatusCode.NoContent)
            }
        }
    }
}