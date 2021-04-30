package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage
import java.time.Instant
import kotlin.time.Duration
import kotlin.time.days
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

/**
 * Забанить пользователя на указанное время
 * @param duration время бана
 * @param attackerMessage сообщение с указанием кого банить в ответе
 * @param storage хранилище времени
 */
fun Bot.ban(duration: Duration, attackerMessage: Message, storage: TimeStorage) {

  @Suppress("NAME_SHADOWING")
  val duration = when {
    duration < 30.seconds -> {
      sendTempMessage(
        attackerMessage.chat.id,
        "$duration is too small for telegram api, 30 seconds are used.",
        replyToMessageId = attackerMessage.messageId,
        lifetime = 3.seconds
      )
      30.seconds
    }
    duration > 366.days -> {
      sendTempMessage(
        attackerMessage.chat.id,
        "$duration is too much for telegram api, 366 days are used.",
        replyToMessageId = attackerMessage.messageId,
        lifetime = 3.seconds
      )
      366.days
    }
    else -> duration
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