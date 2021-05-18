import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.slf4j.LoggerFactory.getLogger
import speech.parseCommand
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
        text.parseCommand()?.execute(bot, message)
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