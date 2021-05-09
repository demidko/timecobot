package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage.useTime
import java.time.Instant.now
import kotlin.time.Duration.Companion.seconds

private val freedom = ChatPermissions(
  canSendMessages = true,
  canSendMediaMessages = true,
  canSendPolls = true,
  canSendOtherMessages = true,
  canAddWebPagePreviews = true,
  canChangeInfo = true,
  canInviteUsers = true,
  canPinMessages = true,
)

/**
 * Освободить пользователя из бана
 * @param masterMessage сообщение с указанием кого разблокировать в ответе
 */
fun Bot.free(masterMessage: Message) {
  val master = masterMessage
    .from
    ?.id
    ?: error("You hasn't telegram id")
  val slaveMessage = masterMessage
    .replyToMessage
    ?: error("You need to reply to the user to redeem him")
  val slave = slaveMessage
    .from
    ?.id
    ?: error("You need to reply to the user with telegram id to redeem him")
  val freedomEpochSecond = getChatMember(masterMessage.chat.id, slave)
    .first
    ?.body()
    ?.result
    ?.forceReply
    ?.toLong()
    ?: error("This user already free")
  useTime(master, seconds(freedomEpochSecond - now().epochSecond)) {
    restrictChatMember(masterMessage.chat.id, slave, freedom)
    sendTempMessage(
      masterMessage.chat.id,
      "You are free now!",
      replyToMessageId = slaveMessage.messageId,
    )
  }
}