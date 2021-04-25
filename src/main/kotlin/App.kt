import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import features.ban
import features.free
import features.status
import features.transfer
import org.slf4j.LoggerFactory.getLogger
import speech.*
import storages.InMemoryTimeBank
import storages.TimeBank
import java.lang.System.getenv

fun main(args: Array<String>) = bot {
  token = getenv("TOKEN")
  logLevel = Error
  val bank: TimeBank = InMemoryTimeBank
  val log = getLogger("Bot")
  log.info(getenv("DATABASE_URL"))
  dispatch {
    message {
      message.from?.id?.let(bank::register)
    }
    text {
      try {
        when (val command = text.command()) {
          is BanCommand -> bot.ban(command.duration, message, bank)
          is FreeCommand -> bot.free(message, bank)
          is StatusCommand -> bot.status(message, bank)
          is TransferCommand -> bot.transfer(command.duration, message, bank)
        }
      } catch (e: RuntimeException) {
        log.error(e.message, e)
      }
    }
  }
}.startPolling()




