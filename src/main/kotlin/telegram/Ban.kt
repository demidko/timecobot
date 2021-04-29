package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import org.slf4j.LoggerFactory.getLogger
import storages.TimeBank
import java.time.Instant
import java.time.ZoneId.of
import kotlin.time.Duration
import kotlin.time.seconds

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

fun Bot.ban(duration: Duration, attackerMessage: Message, storage: TimeBank) {

  @Suppress("NAME_SHADOWING")
  val duration = if (duration < 30.seconds) {
    sendTempMessage(
      attackerMessage.chat.id,
      "$duration is too small for telegram api, 30 seconds are used.",
      replyToMessageId = attackerMessage.messageId,
      lifetime = 3.seconds
    )
    30.seconds
  } else {
    duration
  }

  val attacker = attackerMessage
    .from
    ?.id
    ?: error("You hasn't telegram id")
  val victimMessage = attackerMessage
    .replyToMessage
    ?: error("You need to reply to the user to ban him")
  val victim = victimMessage
    .from
    ?.id
    ?: error("You need to reply to the user with telegram id to ban him")
  storage.use(attacker, duration) {

    val untilSecond = Instant.now().epochSecond + it.inSeconds.toLong()

    restrictChatMember(
      attackerMessage.chat.id,
      victim,
      customBan,
      untilSecond
    )
    sendTempMessage(
      attackerMessage.chat.id,
      "💥",
      replyToMessageId = victimMessage.messageId,
    )
  }
  delayDeleteMessage(attackerMessage.chat.id, attackerMessage.messageId)
}