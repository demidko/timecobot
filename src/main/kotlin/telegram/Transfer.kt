package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage
import kotlin.time.Duration


/**
 * Перевести другомуц пользователю свое время
 * @param duration время для перевода
 * @param senderMessage сообщение с указанием кому перевести время
 * @param storage хранилище времени
 */
fun Bot.transfer(duration: Duration, senderMessage: Message, storage: TimeStorage) {
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
  storage.transfer(sender, recipient, duration) {
    sendTempMessage(
      senderMessage.chat.id,
      "+$duration",
      replyToMessageId = recipientMessage.messageId,
    )
  }
  delayDeleteMessage(senderMessage.chat.id, senderMessage.messageId)
}