import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import org.slf4j.LoggerFactory.getLogger
import speech.*
import telegram.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.timer
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

private val log = getLogger("Router")

private val perHourStat = AtomicLong().apply {
  timer(period = hours(1).inWholeMilliseconds) {
    log.info("${getAndSet(0)} recognized requests per hour")
  }
}

fun TextHandlerEnvironment.routeRequest() {
  var elapsedTime = 0L
  try {
    elapsedTime = measureTimeMillis {
      when (val command = text.parseCommand()) {
        is BanCommand -> bot.ban(command.duration, message)
        is FreeCommand -> bot.free(message)
        is StatusCommand -> bot.status(message)
        is TransferCommand -> bot.transfer(command.duration, message)
        is HelpCommand -> bot.help(message)
        else -> return
      }
    }
  } finally {
    if (elapsedTime > 500) {
      log.warn("Too large request processed ${milliseconds(elapsedTime)}: $text")
    }
  }
  perHourStat.incrementAndGet()
}