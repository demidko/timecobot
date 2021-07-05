package semnorms.commands.durable

import com.github.kotlintelegrambot.entities.ChatId
import Query
import semnorms.commands.Durable
import semnorms.stem
import semnorms.word
import kotlin.time.Duration

object Pin : Durable(stem("закреп", "pin", "запин"), word("пин")) {

  override fun execute(query: Query, duration: Duration): Unit = query.run {
    val user = message.from?.id ?: return
    val messageId = message.replyToMessage?.messageId ?: return
    storage.use(duration, user) {
      //bot.pinChatMessageTemporary(storage, message.chat.id, messageId, duration)
    }
    val chatId = ChatId.fromId(message.chat.id)
    bot.pinChatMessage(chatId, messageId)
  }
}