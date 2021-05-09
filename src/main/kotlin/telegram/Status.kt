package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage.seeTime

/** Показать баланс */
fun Bot.status(m: Message) {
  val user = m.from?.id ?: error("You hasn't telegram id")
  sendTempMessage(m.chat.id, "You have ${seeTime(user)}", replyToMessageId = m.messageId)
}