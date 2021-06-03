package semnorms.commands.durable

import PinnedMessages
import Timecoins
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import pinChatMessageTemporary
import semnorms.commands.Durable
import semnorms.stem
import semnorms.word
import using
import kotlin.time.Duration

object Pin : Durable(stem("закреп", "pin", "запин"), word("пин")) {

  override fun execute(
    bot: Bot,
    message: Message,
    duration: Duration,
    coins: Timecoins,
    pins: PinnedMessages
  ) {
    val user = message.from?.id ?: return
    val messageId = message.replyToMessage?.messageId ?: return
    coins.using(user, duration) {
      bot.pinChatMessageTemporary(pins, message.chat.id, messageId, duration)
    }
    val chatId = ChatId.fromId(message.chat.id)
    bot.pinChatMessage(chatId, messageId)
  }
}