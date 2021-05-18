package commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
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
 * The command to ban another user
 * @param duration ban duration
 */
@JvmInline
value class Ban(val duration: Duration) : Command {

  /**
   * Ban a user for a specified time
   * @param bot bot
   * @param m message about who to ban in reply
   */
  override fun execute(bot: Bot, m: Message) {

    val victimMessage = m.replyToMessage ?: return
    val duration = when {
      duration < seconds(30) -> {
        bot.sendTempMessage(
          m.chat.id,
          "$duration is too small for telegram api, 30 seconds are used.",
          replyToMessageId = m.messageId,
        )
        seconds(30)
      }
      duration > days(366) -> {
        bot.sendTempMessage(
          m.chat.id,
          "${duration.print()} is too much for telegram api, 1yr 1d are used",
          replyToMessageId = m.messageId,
        )
        days(366)
      }
      else -> duration
    }

    val attacker = m.from?.id ?: error("You hasn't telegram id")
    val victim =
      victimMessage.from
        ?.id
        ?: error("You need to reply to the user with telegram id to ban him")

    val freedomEpochSecond = bot.getChatMember(fromId(m.chat.id), victim)
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
      bot.restrictChatMember(
        fromId(m.chat.id),
        victim,
        customBan,
        untilSecond
      )
      bot.sendTempMessage(
        m.chat.id,
        "💥",
        replyToMessageId = victimMessage.messageId,
      )
    }
  }
}

