package features

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import print
import storages.TimeStorage.useTime
import utils.sendTempMessage
import java.time.Instant.now
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.seconds

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

/**
 * Ban a user for a specified time
 * @param duration ban time
 * @param attackerMessage message about who to ban in reply
 */
fun Bot.ban(duration: Duration, attackerMessage: Message) {

  @Suppress("NAME_SHADOWING")
  val duration = when {
    duration < seconds(30) -> {
      sendTempMessage(
        attackerMessage.chat.id,
        "$duration is too small for telegram api, 30 seconds are used.",
        replyToMessageId = attackerMessage.messageId,
      )
      seconds(30)
    }
    duration > days(366) -> {
      sendTempMessage(
        attackerMessage.chat.id,
        "${duration.print()} is too much for telegram api, 1yr 1d are used",
        replyToMessageId = attackerMessage.messageId,
      )
      days(366)
    }
    else -> duration
  }

  val attacker = attackerMessage.from?.id ?: error("You hasn't telegram id")
  val victimMessage =
    attackerMessage.replyToMessage ?: error("You need to reply to the user to ban him")
  val victim =
    victimMessage.from
      ?.id
      ?: error("You need to reply to the user with telegram id to ban him")

  val freedomEpochSecond = getChatMember(fromId(attackerMessage.chat.id), victim)
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

  useTime(attacker, duration) {

    restrictChatMember(
      fromId(attackerMessage.chat.id),
      victim,
      customBan,
      untilSecond
    )
    sendTempMessage(
      attackerMessage.chat.id,
      "💥",
      replyToMessageId = victimMessage.messageId,
    )

    // attack permanent notification in chat
    val attackerName = attackerMessage.from?.firstName ?: "id$attacker"
    val logMessage = "[$attackerName](tg://user?id=$attacker) 💥 ${duration.print()}"
    sendMessage(
      fromId(attackerMessage.chat.id),
      logMessage,
      parseMode = MARKDOWN_V2,
      replyToMessageId = victimMessage.messageId
    )
  }
}