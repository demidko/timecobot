import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import org.slf4j.LoggerFactory.getLogger
import speech.*
import storages.StatsStorage.addRequestStat
import telegram.*
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.milliseconds

private val log = getLogger("Router")

fun TextHandlerEnvironment.routeRequest() {
  var elapsedTimeMs = 0L
  var useStat = true
  try {
    elapsedTimeMs = measureTimeMillis {
      when (val command = text.command()) {
        is BanCommand -> bot.ban(command.duration, message)
        is FreeCommand -> bot.unban(message)
        is StatusCommand -> bot.status(message)
        is TransferCommand -> bot.transfer(command.duration, message)
        is HelpCommand -> bot.help(message)
        else -> useStat = false
      }
    }
  } finally {
    if (elapsedTimeMs > 500) {
      log.warn("Too large request processed ${milliseconds(elapsedTimeMs)}: $text")
    }
    if (useStat) {
      addRequestStat(elapsedTimeMs)
    }
  }
}
