package features

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import storages.TimeStorage.transferTime
import print
import utils.sendTempMessage
import kotlin.time.Duration


/**
 * Transfer some of your time to another user
 * @param duration time (duration) for transfer
 * @param senderMessage message indicating to whom to transfer the time
 */
fun Bot.transfer(duration: Duration, senderMessage: Message) {
  val sender = senderMessage
    .from
    ?.id
    ?: error("You hasn't telegram id")
  val recipientMessage =
    senderMessage.replyToMessage ?: error("You need to reply to the user to transfer time to him")
  val recipient = recipientMessage
    .from
    ?.id
    ?: error("Your need to reply to the user with telegram id to transfer time to him")
  transferTime(sender, recipient, duration) {
    sendTempMessage(
      senderMessage.chat.id,
      "+${duration.print()}",
      replyToMessageId = recipientMessage.messageId,
    )
  }
}