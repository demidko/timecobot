import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import storages.TimecoinStorage
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.seconds

private val ban = ChatPermissions(
  canSendMessages = false,
  canSendMediaMessages = false,
  canSendPolls = false,
  canSendOtherMessages = false,
  canAddWebPagePreviews = false,
  canChangeInfo = false,
  canInviteUsers = true,
  canPinMessages = false,
)

fun Bot.ban(duration: Duration, data: Message, storage: TimecoinStorage) {

  val attacker = data.from?.id
    ?: error("You hasn't telegram id")
  val victimMessage = data.replyToMessage
    ?: error("You need to reply to the user to ban him")
  val victim = victimMessage.from?.id
    ?: error("You need to reply to the user with telegram id to ban him")

  storage.use(attacker, duration) {
    restrictChatMember(data.chat.id, victim, ban, Instant.now().epochSecond + it.inSeconds.toLong())
    sendMessage(
      data.chat.id,
      "💥",
      replyToMessageId = victimMessage.messageId,
      lifetime = 15.seconds
    )
  }

  deleteMessage(data.chat.id, data.messageId, 15.seconds)
}