package telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import debug
import org.slf4j.LoggerFactory
import speech.*
import storages.TimeStorage
import java.lang.System.getenv
import kotlin.time.Duration.Companion.seconds

/** @return new @timecobot instance */
fun timecobot() = bot {
  val bank = TimeStorage()
  val log = LoggerFactory.getLogger("Bot")
  token = getenv("TOKEN")
  logLevel = Error
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
          is HelpCommand -> bot.help(message)
          is DebugCommand -> bot.debug(message)
        }
      } catch (e: RuntimeException) {
        bot.sendTempMessage(
          message.chat.id,
          e.message ?: "Oops... Something is wrong 🤔",
          replyToMessageId = message.messageId,
          lifetime = seconds(3)
        )
        log.error(text, e)
        bot.delayDeleteMessage(message.chat.id, message.messageId)
      }
    }
  }
}

