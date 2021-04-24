import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import java.util.*
import kotlin.concurrent.schedule
import kotlin.time.Duration
import kotlin.time.seconds

private val timer = Timer()

/**
 * Use this method to send text messages
 * @param chatId unique identifier for the target chat
 * @param text text of the message to be sent, 1-4096 characters after entities parsing
 * @param parseMode mode for parsing entities in the message text
 * @param disableWebPagePreview disables link previews for links in this message
 * @param disableNotification sends the message silently - users will receive a notification with no sound
 * @param replyToMessageId if the message is a reply, ID of the original message
 * @param replyMarkup additional options - inline keyboard, custom reply keyboard, instructions to remove reply
 * keyboard or to force a reply from the user
 * @param lifetime message lifetime
 * @return the sent [Message] on success
 */
fun Bot.sendTempMessage(
  chatId: Long,
  text: String,
  parseMode: ParseMode? = null,
  disableWebPagePreview: Boolean? = null,
  disableNotification: Boolean? = null,
  replyToMessageId: Long? = null,
  replyMarkup: ReplyMarkup? = null,
  lifetime: Duration = 15.seconds
) {

  val messageSendingResult = sendMessage(
    chatId,
    text,
    parseMode,
    disableWebPagePreview,
    disableNotification,
    replyToMessageId,
    replyMarkup
  )

  val messageId = messageSendingResult.first
    ?.body()
    ?.result
    ?.messageId
    ?: error("Failed to send message")

  timer.schedule(lifetime.toLongMilliseconds()) {
    deleteMessage(chatId, messageId)
  }
}