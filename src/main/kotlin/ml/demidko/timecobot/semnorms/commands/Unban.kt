package ml.demidko.timecobot.semnorms.commands

import ml.demidko.timecobot.Query
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ChatPermissions
import ml.demidko.timecobot.semnorms.Executable
import ml.demidko.timecobot.semnorms.stem
import sendTempMessage
import java.time.Instant
import kotlin.time.Duration

/** Semantic representation of a redeem request */
object Unban : Executable(
  stem(
    "liberat",
    "heal",
    "ransom",
    "atonement",
    "expiation",
    "redemption",
    "rescue",
    "salvation",
    "redeem",
    "unblock",
    "unban",
    "unmute",
    "разбан",
    "разблок",
    "ожив",
    "выкуп",
    "исцел",
    "искуп",
    "спаст",
    "освобод"
  )
) {

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

  override fun execute(query: Query) {
    val slaveMessage =
      query.message.replyToMessage ?: return
    val master = query.message
      .from
      ?.id
      ?: return
    val slave = slaveMessage
      .from
      ?.id
      ?: return

    val freedomEpochSecond = query.bot.getChatMember(fromId(query.message.chat.id), slave)
      .first
      ?.body()
      ?.result
      ?.forceReply
      ?.toLong()
      ?: error("This user already free")

    val banDurationSec = freedomEpochSecond - Instant.now().epochSecond

    if (banDurationSec < 0) {
      error("This user already free")
    }

    query.storage.use(Duration.seconds(banDurationSec), master) {
      query.bot.restrictChatMember(fromId(query.message.chat.id), slave, freedom)
      query.bot.sendTempMessage(
        query.message.chat.id,
        "You are free now!",
        replyToMessageId = slaveMessage.messageId
      )
    }
  }
}