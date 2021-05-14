package features

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage
import toHumanView
import utils.sendTempMessage

/** See balance */
fun Bot.balance(m: Message) {

  val balance =
    m.from
      ?.id
      ?.let(TimeStorage::seeTimeInWholeSeconds)
      ?.toHumanView()
      ?: error("You hasn't telegram id")

  sendTempMessage(m.chat.id, balance, replyToMessageId = m.messageId)
}
