import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage.useTime
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.Duration

private val timer = Timer()

/** pin */
fun Bot.pin(duration: Duration, m: Message) {
  val user = m.from?.id ?: error("You hasn't telegram id")
  val chatId = fromId(m.chat.id)
  val messageId = m.replyToMessage?.messageId ?: error("You need to reply to the message to pin it")
  useTime(user, duration) {
    pinChatMessage(chatId, messageId)
    timer.schedule(duration.inWholeMilliseconds) {
      unpinChatMessage(chatId, messageId)
    }
  }
}