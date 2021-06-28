import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.entities.ReplyMarkup
import com.github.kotlintelegrambot.logging.LogLevel.Error
import ml.demidko.timecobot.Query
import ml.demidko.timecobot.Storage
import org.slf4j.LoggerFactory.getLogger
import java.lang.System.currentTimeMillis
import java.time.Instant
import java.util.*
import kotlin.concurrent.schedule
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private val log = getLogger("Timecobot")
private val timer = Timer()

fun Bot(apiToken: String, storage: Storage) =
  bot {
    token = apiToken
    logLevel = Error
    dispatch {
      text {
        val fromId = message.from?.id ?: return@text
        val timestamp = currentTimeMillis()
        try {
          storage.registerUser(fromId)
          Query(bot, storage, message).execute()
        } catch (e: RuntimeException) {
          log.error(text, e)
        } finally {
          val elapsedMs = currentTimeMillis() - timestamp
          //if (elapsedMs > 500) {
          log.warn("(${elapsedMs}ms): $text")
          //}
        }
      }
    }
  }.apply {
    /**
     * The task of periodic unpinning obsolete messages
     */
    timer(period = Duration.minutes(1).inWholeMilliseconds) {
      val currentEpochSecond = Instant.now().epochSecond
      storage.pinnedMessages.access {
        for (chat in it) {
          val chatId = fromId(chat.key)
          val unpaidMessages = mutableListOf<Long>()
          for (message in chat.value) {
            if (message.value >= currentEpochSecond) {
              unpaidMessages.add(message.key)
            }
          }
          for (message in unpaidMessages) {
            unpinChatMessage(chatId, message)
            chat.value.remove(message)
          }
        }
      }
      log.info("Unpaid messages are unpinned")
    }
  }


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
 */
fun Bot.sendTempMessage(
  chatId: Long,
  text: String,
  parseMode: ParseMode? = null,
  disableWebPagePreview: Boolean? = null,
  disableNotification: Boolean? = true,
  replyToMessageId: Long? = null,
  replyMarkup: ReplyMarkup? = null,
  lifetime: Duration = seconds(15)
) {

  val messageSendingResult = sendMessage(
    fromId(chatId),
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

  delayDeleteMessage(chatId, messageId, lifetime)
}

/**
 * Use this method to delete a message, including service messages, with the following limitations:
 * - A message can only be deleted if it was sent less than 48 hours ago.
 * - A dice message in a private chat can only be deleted if it was sent more than 24 hours ago.
 * - Bots can delete outgoing messages in private chats, groups, and supergroups.
 * - Bots can delete incoming messages in private chats.
 * - Bots granted `can_post_messages` permissions can delete outgoing messages in channels.
 * - If the bot is an administrator of a group, it can delete any message there.
 * - If the bot has `can_delete_messages` permission in a supergroup or a channel, it can delete any message there.
 * @param chatId Unique identifier for the target chat.
 * @param messageId Identifier of the message to delete.
 * @param delay lifetime of the message to delete
 * @return True on success.
 */
fun Bot.delayDeleteMessage(chatId: Long, messageId: Long, delay: Duration = seconds(15)) =
  timer.schedule(delay.inWholeMilliseconds) {
    deleteMessage(fromId(chatId), messageId)
  }


fun Bot.restrictChatAdmin(
  chatId: ChatId,
  userId: Long,
  chatPermissions: ChatPermissions,
  untilEpochSecond: Long,
  storage: Storage
) {
  if (userId in listAdminIds(chatId)) {
    storage.muteUser((chatId as ChatId.Id).id, userId, untilEpochSecond)
  } else {
    restrictChatMember(chatId, userId, chatPermissions, untilEpochSecond)
  }
}

fun Bot.listAdminIds(chatId: ChatId) = getChatAdministrators(chatId)
  .getOrDefault(listOf())
  .map { it.user.id }

fun Bot.pinChatMessageTemporary(db: Storage, chat: Long, m: Long, duration: Duration) =
  db.pinnedMessages.access {
    it.fastPutIfAbsent(chat, mutableMapOf())
    pinChatMessage(fromId(chat), m)
  }


