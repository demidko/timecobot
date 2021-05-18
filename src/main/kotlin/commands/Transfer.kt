package commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import print
import storages.TimeStorage.transferTime
import utils.sendTempMessage
import kotlin.time.Duration


/**
 * Command to transfer time to another user
 * @param duration time duration for transfer
 */
@JvmInline
value class Transfer(val duration: Duration) : Command {

  /**
   * Transfer some of your time to another user
   * @param bot bot
   * @param m message indicating to whom to transfer the time
   */
  override fun execute(bot: Bot, m: Message) {
    val recipientMessage =
      m.replyToMessage ?: return
    val sender = m
      .from
      ?.id
      ?: error("You hasn't telegram id")
    val recipient = recipientMessage
      .from
      ?.id
      ?: error("Your need to reply to the user with telegram id to transfer time to him")
    transferTime(sender, recipient, duration) {
      bot.sendTempMessage(
        m.chat.id,
        "+${duration.print()}",
        replyToMessageId = recipientMessage.messageId,
      )
    }
  }
}
