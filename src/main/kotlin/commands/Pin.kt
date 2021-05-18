package commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.Message
import commands.Command

/** Pin message */
object Pin : Command {
  override fun execute(bot: Bot, m: Message) {
    val messageId = m.replyToMessage?.messageId ?: return
    val chatId = fromId(m.chat.id)
    bot.pinChatMessage(chatId, messageId)
  }
}