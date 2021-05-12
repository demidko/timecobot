import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.slf4j.LoggerFactory.getLogger
import storages.TimeStorage
import telegram.sendTempMessage
import java.lang.System.getenv
import kotlin.time.Duration.Companion.seconds

/** @return new @timecobot instance */
fun timecobot() = bot {
  val log = getLogger("Bot")
  token = getenv("TOKEN")
  logLevel = Error
  timeout
  dispatch {
    message {
      message.from?.id?.let(TimeStorage::registerUser)
    }
    text {
      try {
        routeRequest()
      } catch (e: RuntimeException) {
        bot.sendTempMessage(
          message.chat.id,
          e.message ?: "Oops... Something is wrong 🤔",
          replyToMessageId = message.messageId,
          lifetime = seconds(7)
        )
        log.warn(text, e)
      }
    }
  }
}

