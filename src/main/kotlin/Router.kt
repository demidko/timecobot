import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import org.slf4j.LoggerFactory.getLogger
import speech.*
import telegram.*
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds

private val log = getLogger("Router")

fun TextHandlerEnvironment.routeRequest() {
  val elapsedTime = measureTimeMillis {
    when (val command = text.parseCommand()) {
      is BanCommand -> bot.ban(command.duration, message)
      is FreeCommand -> bot.free(message)
      is StatusCommand -> bot.status(message)
      is TransferCommand -> bot.transfer(command.duration, message)
      is HelpCommand -> bot.help(message)
    }
  }
  if (elapsedTime > 500) {
    log.warn("Too large request processed ${milliseconds(elapsedTime)}: $text")
  }
}