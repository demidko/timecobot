import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.Message


/** pin */
fun Bot.pin(m: Message) {
  val chatId = fromId(m.chat.id)
  val messageId = m.replyToMessage?.messageId ?: error("You need to reply to the message to pin it")
  pinChatMessage(chatId, messageId)
}