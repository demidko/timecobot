package features

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage
import printSeconds
import utils.sendTempMessage

/** See balance */
fun Bot.balance(m: Message) {

  val balance =
    m.from
      ?.id
      ?.let(TimeStorage::seeTimeInWholeSeconds)
      ?.printSeconds()
      ?: error("You hasn't telegram id")

  if (m.chat.id == m.from?.id) {
    sendMessage(fromId(m.chat.id), balance, replyToMessageId = m.messageId)
  } else {
    sendTempMessage(m.chat.id, balance, replyToMessageId = m.messageId)
  }
}
