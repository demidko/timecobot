package semnorms.commands

import PinnedMessages
import Timecoins
import Token
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import semnorms.Executable
import semnorms.stem
import sendTempMessage
import using
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

  override fun execute(
    token: Iterator<Token>,
    bot: Bot,
    message: Message,
    coins: Timecoins,
    pins: PinnedMessages
  ) {
    val slaveMessage =
      message.replyToMessage ?: return
    val master = message
      .from
      ?.id
      ?: return
    val slave = slaveMessage
      .from
      ?.id
      ?: return

    val freedomEpochSecond = bot.getChatMember(ChatId.fromId(message.chat.id), slave)
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

    coins.using(master, Duration.seconds(banDurationSec)) {
      bot.restrictChatMember(ChatId.fromId(message.chat.id), slave, freedom)
      bot.sendTempMessage(
        message.chat.id,
        "You are free now!",
        replyToMessageId = slaveMessage.messageId
      )
    }
  }
}

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