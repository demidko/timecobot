import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple
import io.ktor.application.*
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = application().main(args)

fun application() = object : CliktCommand("Application description here") {

  val opt by argument().multiple()

  override fun run() {
    echo(opt)
    localhostServer().start(true)
  }
}

fun localhostServer() = embeddedServer(Netty) {
  routing {
    get("/") { call.respond(OK) }
  }
}