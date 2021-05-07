package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage.transferTime
import kotlin.time.Duration


/**
 * Перевести другому пользователю часть своего времени
 * @param duration время для перевода
 * @param senderMessage сообщение с указанием кому перевести время
 * @param storage хранилище времени
 */
fun Bot.transfer(duration: Duration, senderMessage: Message) {
  val sender = senderMessage
    .from
    ?.id
    ?: error("You hasn't telegram id")
  val recipientMessage = senderMessage
    .replyToMessage
    ?: error("You need to reply to the user to transfer time to him")
  val recipient = recipientMessage
    .from
    ?.id
    ?: error("You need to reply to the user with telegram id to transfer time to him")
  transferTime(sender, recipient, duration) {
    sendTempMessage(
      senderMessage.chat.id,
      "+$duration",
      replyToMessageId = recipientMessage.messageId,
    )
  }
  delayDeleteMessage(senderMessage.chat.id, senderMessage.messageId)
}