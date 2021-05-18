package commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.Message
import printSeconds
import storages.TimeStorage
import utils.sendTempMessage

/** Team to check your status */
object Balance : Command {
  override fun execute(bot: Bot, m: Message) {
    val balance =
      m.from
        ?.id
        ?.let(TimeStorage::seeTimeInWholeSeconds)
        ?.printSeconds()
        ?: error("You hasn't telegram id")
    if (m.chat.id == m.from?.id) {
      bot.sendMessage(fromId(m.chat.id), balance, replyToMessageId = m.messageId)
    } else {
      bot.sendTempMessage(m.chat.id, balance, replyToMessageId = m.messageId)
    }
  }
}
