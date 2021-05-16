import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.slf4j.LoggerFactory.getLogger
import speech.command
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
        text.command()?.execute(bot, message)
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

    message {
      if (message.chat.type in listOf("group", "supergroup")) {
        message.chat.apply {
          if (username == null && inviteLink == null) {
            bot.getChat(fromId(message.chat.id)).getOrNull()?.run {
              log.info(
                "${message.text ?: ""}\n{\n" +
                  "  $title $firstName $lastName\n" +
                  "  $bio $description\n" +
                  "  @$username\n" +
                  "  $inviteLink\n" +
                  "}"
              )
            }
          } else {
            log.info(
              "${message.text ?: ""}\n{\n" +
                "  $title $firstName $lastName\n" +
                "  $bio $description\n" +
                "  @$username\n" +
                "  $inviteLink\n" +
                "}"
            )
          }
        }
      }
    }
  }
}.startPolling()