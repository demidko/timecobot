package commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage.useTime
import utils.sendTempMessage
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

/** The command to buy another user out of the ban */
object Unban : Command {
  /**
   * Unban user
   * @param m a message indicating who to unblock in the reply
   */
  override fun execute(bot: Bot, m: Message) {
    val slaveMessage =
      m.replyToMessage ?: return
    val master = m
      .from
      ?.id
      ?: error("You hasn't telegram id")
    val slave = slaveMessage
      .from
      ?.id
      ?: error("Your need to reply to the user with telegram id to redeem him")

    val freedomEpochSecond = bot.getChatMember(fromId(m.chat.id), slave)
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
      bot.restrictChatMember(fromId(m.chat.id), slave, freedom)
      bot.sendTempMessage(
        m.chat.id,
        "You are free now!",
        replyToMessageId = slaveMessage.messageId
      )
    }
  }
}
