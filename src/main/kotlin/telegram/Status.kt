package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage

fun Bot.status(m: Message, storage: TimeStorage) {
  val user = m.from?.id ?: error("You hasn't telegram id")
  sendTempMessage(m.chat.id, "You have ${storage.status(user)}", replyToMessageId = m.messageId)
  delayDeleteMessage(m.chat.id, m.messageId)
}