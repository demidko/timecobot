package features

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import storages.TimeBank
import telegram.delayDeleteMessage
import telegram.sendTempMessage

fun Bot.status(masterMessage: Message, storage: TimeBank) {
  val master = masterMessage
    .from
    ?.id
    ?: error("You hasn't telegram id")
  sendTempMessage(masterMessage.chat.id, "You have ${storage.status(master)}")
  delayDeleteMessage(masterMessage.chat.id, masterMessage.messageId)
}