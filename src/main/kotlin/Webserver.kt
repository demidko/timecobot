import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun newWebserver() = embeddedServer(Netty) {
  routing {
    get("/") { call.respond(OK) }
  }
}