package semnorms.commands

import PinnedMessages
import Timecoins
import Token
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import printSeconds
import seconds
import semnorms.Executable
import semnorms.stem
import sendTempMessage

/** Semantic representation of balance request */
object Balance : Executable(
  stem(
    "time",
    "врем",
    "balance",
    "status",
    "score",
    "coins",
    "баланс",
    "статус",
    "счет",
    "счёт",
    "узна",
    "timecoin",
    "check"
  )
) {

  override fun execute(
    token: Iterator<Token>,
    bot: Bot,
    message: Message,
    coins: Timecoins,
    pins: PinnedMessages
  ) {

    val balance =
      message.from
        ?.id
        ?.let(coins::seconds)
        ?.printSeconds()
        ?: return
    if (message.chat.id == message.from?.id) {
      bot.sendMessage(ChatId.fromId(message.chat.id), balance, replyToMessageId = message.messageId)
    } else {
      bot.sendTempMessage(message.chat.id, balance, replyToMessageId = message.messageId)
    }
  }
}