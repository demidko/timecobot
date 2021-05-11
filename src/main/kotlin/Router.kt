import LastHourStat.addRequestStat
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

private object LastHourStat {

  private val perHourRequests = AtomicLong()
  private val perHourTime = AtomicLong()

  init {
    timer(period = hours(1).inWholeMilliseconds) {
      val requests = perHourRequests.getAndSet(0)
      val time = perHourTime.getAndSet(0)
      log.info("$requests recognized requests per hour (${time / requests}ms on average)")
    }
  }

  fun addRequestStat(elapsedTimeMs: Long) {
    perHourRequests.incrementAndGet()
    perHourTime.addAndGet(elapsedTimeMs)
  }
}

fun TextHandlerEnvironment.routeRequest() {
  var elapsedTimeMs = 0L
  var useStat = true
  try {
    elapsedTimeMs = measureTimeMillis {
      when (val command = text.parseCommand()) {
        is BanCommand -> bot.ban(command.duration, message)
        is FreeCommand -> bot.free(message)
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
