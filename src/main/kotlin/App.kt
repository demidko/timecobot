import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import features.*
import org.slf4j.LoggerFactory.getLogger
import speech.*
import storages.TimeStorage
import utils.sendTempMessage
import java.lang.System.currentTimeMillis
import java.lang.System.getenv
import kotlin.time.Duration.Companion.seconds

fun main() = bot {
  val log = getLogger("Bot")
  token = getenv("TOKEN")
  logLevel = Error
  dispatch {
    text {
      val timestamp = currentTimeMillis()
      try {
        message.from?.id?.let(TimeStorage::registerUser)
        when (val command = text.command()) {
          is BanCommand -> bot.ban(command.duration, message)
          is FreeCommand -> bot.unban(message)
          is StatusCommand -> bot.balance(message)
          is TransferCommand -> bot.transfer(command.duration, message)
          is HelpCommand -> bot.help(message)
          is PinCommand -> bot.pin(command.duration, message)
        }
      } catch (e: RuntimeException) {
        log.error(text, e)
        bot.sendTempMessage(
          message.chat.id,
          e.message ?: "Oops... Something is wrong 🤔",
          replyToMessageId = message.messageId,
          lifetime = seconds(7)
        )
      } finally {
        val elapsedMs = currentTimeMillis() - timestamp
        if (elapsedMs > 500) {
          log.warn("Too large message processed ${elapsedMs}ms: $text")
        }
      }
    }
  }
}.startPolling()