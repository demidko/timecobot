package telegram

import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import features.ban
import features.free
import features.status
import features.transfer
import org.slf4j.Logger
import speech.*
import storages.TimeBank
import java.lang.System.getenv

fun timecoinsBot(bank: TimeBank, log: Logger) = bot {
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
        }
      } catch (e: RuntimeException) {
        bot.sendTempMessage(
          message.chat.id,
          e.message ?: "Internal exception",
          replyToMessageId = message.messageId
        )
        log.error(e.message, e)
      }
    }
  }
}

