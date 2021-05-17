package features

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import org.slf4j.LoggerFactory.getLogger
import storages.TimeStorage.useTime
import utils.sendTempMessage
import java.time.Instant.now
import kotlin.time.Duration.Companion.seconds

private val log = getLogger("Unban")

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
 * Unban user
 * @param masterMessage a message indicating who to unblock in the reply
 */
fun Bot.unban(masterMessage: Message) {
  val master = masterMessage
    .from
    ?.id
    ?: error("You hasn't telegram id")
  val slaveMessage =
    masterMessage.replyToMessage ?: error("You need to reply to the user to ransom him")
  val slave = slaveMessage
    .from
    ?.id
    ?: error("Your need to reply to the user with telegram id to redeem him")

  val freedomEpochSecond = getChatMember(fromId(masterMessage.chat.id), slave)
    .first
    ?.body()
    ?.result
    ?.forceReply
    ?.toLong()
    ?: error("This user already free")

  val banDurationSec = freedomEpochSecond - now().epochSecond

  if (banDurationSec < 0) {
    error("This user already free")
  }

  useTime(master, seconds(banDurationSec)) {
    restrictChatMember(fromId(masterMessage.chat.id), slave, freedom)
    sendTempMessage(
      masterMessage.chat.id,
      "You are free now!",
      replyToMessageId = slaveMessage.messageId
    )
  }
}