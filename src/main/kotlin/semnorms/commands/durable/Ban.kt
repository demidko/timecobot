package semnorms.commands.durable

import com.github.demidko.print.utils.print
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import com.github.kotlintelegrambot.entities.ChatPermissions
import Query
import semnorms.commands.Durable
import semnorms.stem
import java.time.Instant.now
import java.util.*
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

private val timer = Timer()

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

  override fun execute(query: Query, duration: Duration): Unit = query.run {

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

    val freedomEpochSecond = bot.getChatMember(fromId(message.chat.id), victim)
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

    val chatId = fromId(message.chat.id)
    storage.use(duration, attacker) {
      val admins = bot.getChatAdministrators(chatId).getOrDefault(listOf())

      bot.restrictChatMember(
        chatId,
        victim,
        customBan,
        untilSecond
      )
      bot.sendMessage(
        fromId(message.chat.id),
        "💥 ${duration.print()}",
        replyToMessageId = victimMessage.messageId,
      )
    }
  }
}