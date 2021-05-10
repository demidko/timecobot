package telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.slf4j.LoggerFactory.getLogger
import speech.*
import storages.TimeStorage
import java.lang.System.getenv
import kotlin.time.Duration.Companion.seconds

/** @return new @timecobot instance */
fun timecobot() = bot {
  val log = getLogger("Bot")
  token = getenv("TOKEN")
  logLevel = Error
  dispatch {
    text {
      message.from?.id?.let(TimeStorage::registerUser)
      try {
        when (val command = text.command()) {
          is BanCommand -> bot.ban(command.duration, message)
          is FreeCommand -> bot.free(message)
          is StatusCommand -> bot.status(message)
          is TransferCommand -> bot.transfer(command.duration, message)
          is HelpCommand -> bot.help(message)
        }
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

