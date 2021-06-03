package semnorms.commands.durable

import PinnedMessages
import Timecoins
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import print
import semnorms.commands.Durable
import semnorms.stem
import sendTempMessage
import using
import java.time.Instant.now
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

/** Semantic representation of a ban request */
object Ban : Durable(
  stem(
    "ban",
    "block",
    "freez",
    "mute",
    "бан",
    "блок",
    "забан",
    "заглох",
    "завали",
    "умри",
    "умер",
    "мьют",
    "замьют",
    "💥"
  )
) {

  override fun execute(
    bot: Bot,
    message: Message,
    duration: Duration,
    coins: Timecoins,
    pins: PinnedMessages
  ) {

    val victimMessage = message.replyToMessage ?: return
    val attacker = message.from?.id ?: return
    val victim =
      victimMessage.from
        ?.id
        ?: return

    if (duration < seconds(30)) {
      error("$duration is too ban for Telegram API!")
    }
    if (duration > days(366)) {
      error("${duration.print()} is too much for Telegram API.")
    }

    val freedomEpochSecond = bot.getChatMember(ChatId.fromId(message.chat.id), victim)
      .first
      ?.body()
      ?.result
      ?.forceReply
      ?.toLong()

    val currentEpochSecond = now().epochSecond

    val previousBanDurationSec = when (freedomEpochSecond) {
      null -> 0L
      else -> {
        val banDurationSec = freedomEpochSecond - currentEpochSecond
        when (banDurationSec > 0) {
          true -> banDurationSec
          else -> 0L
        }
      }
    }

    val untilSecond = currentEpochSecond + previousBanDurationSec + duration.inWholeSeconds

    coins.using(attacker, duration)
    {
      bot.restrictChatMember(
        ChatId.fromId(message.chat.id),
        victim,
        customBan,
        untilSecond
      )
      bot.sendTempMessage(
        message.chat.id,
        "💥",
        replyToMessageId = victimMessage.messageId,
      )
    }
  }
}

private val customBan = ChatPermissions(
  canSendMessages = false,
  canSendMediaMessages = false,
  canSendPolls = false,
  canSendOtherMessages = false,
  canAddWebPagePreviews = false,
  canChangeInfo = false,
  canInviteUsers = true,
  canPinMessages = false,
)