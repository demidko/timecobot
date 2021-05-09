package telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.slf4j.LoggerFactory.getLogger
import speech.*
import stats.DebugStorage.debugGroup
import storages.TimeStorage
import java.lang.System.getenv
import kotlin.time.Duration.Companion.seconds

/** @return new @timecobot instance */
fun timecobot() = bot {
  val log = getLogger("Bot")
  token = getenv("TOKEN")
  logLevel = Error
  dispatch {

    message {
      message.from?.id?.let(TimeStorage::registerUser)
      when (message.chat.type) {
        "group", "supergroup" -> debugGroup(message.chat.id)
      }
    }

    text {
      log.info("${message.chat.id}(${message.chat.title}) —  $text")
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
          "${(e.message ?: "Oops... Something is wrong 🤔")}\nContact @free_kotlin please",
          replyToMessageId = message.messageId,
          lifetime = seconds(7)
        )
        log.error(text, e)
      }
    }
  }
}

