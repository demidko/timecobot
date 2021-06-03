package semnorms.commands.durable

import PinnedMessages
import Timecoins
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import print
import semnorms.commands.Durable
import semnorms.stem
import semnorms.word
import sendTempMessage
import transfer
import kotlin.time.Duration

/** Semantic representation of a money transfer request */
object Transfer : Durable(
  word(
    "дать"
  ),
  stem(
    "transfer",
    "give",
    "take",
    "get",
    "keep",
    "держи",
    "бери",
    "возьми",
    "трансфер",
    "перевод",
    "дар",
    "подар",
    "взял",
    "забер",
    "забир",
    "перевед",
    "перевест",
    "отправ",
    "send"
  )
) {

  override fun execute(
    bot: Bot,
    message: Message,
    duration: Duration,
    coins: Timecoins,
    pins: PinnedMessages
  ) {
    val recipientMessage =
      message.replyToMessage ?: return
    val sender = message
      .from
      ?.id
      ?: return
    val recipient = recipientMessage
      .from
      ?.id
      ?: return
    coins.transfer(sender, recipient, duration) {
      bot.sendTempMessage(
        message.chat.id,
        "+${duration.print()}",
        replyToMessageId = recipientMessage.messageId,
      )
    }
  }
}