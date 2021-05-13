package utils

import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import kotlin.time.Duration.Companion.seconds

fun TextHandlerEnvironment.notifyUserAbout(e: Exception) =
  bot.sendTempMessage(
    message.chat.id,
    e.message ?: "Oops... Something is wrong 🤔",
    replyToMessageId = message.messageId,
    lifetime = seconds(7)
  )
