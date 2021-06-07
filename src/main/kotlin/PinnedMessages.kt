import co.touchlab.stately.isolate.IsolateState
import com.github.kotlintelegrambot.Bot
import java.time.Instant
import kotlin.concurrent.schedule
import kotlin.concurrent.timer

private typealias LastEpochSecond = Long

private typealias MessageId = Long

private typealias ChatId = Long

typealias PinnedMessages = IsolateState<MutableMap<ChatId, MutableMap<MessageId, LastEpochSecond>>>

/**
 * The task of periodic unpinning obsolete messages
 */
fun Bot.scheduleUnpinMessages(db: PinnedMessages) =
  timer(period = 1_000) {
    val currentEpochSecond = Instant.now().epochSecond
    db.access { pins ->
      for ((chat, messages) in pins) {
        val chatId = com.github.kotlintelegrambot.entities.ChatId.fromId(chat)
        val deprecatedMessages =
          messages
            .filterValues { it <= currentEpochSecond }
            .keys
        for (messageId in deprecatedMessages) {
          unpinChatMessage(chatId, messageId)
          messages.remove(messageId)
        }
      }
    }
  }