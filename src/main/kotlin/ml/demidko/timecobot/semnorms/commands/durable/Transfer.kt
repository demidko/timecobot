package ml.demidko.timecobot.semnorms.commands.durable

import ml.demidko.timecobot.Query
import com.github.demidko.print.utils.print
import ml.demidko.timecobot.semnorms.commands.Durable
import ml.demidko.timecobot.semnorms.stem
import ml.demidko.timecobot.semnorms.word
import sendTempMessage
import kotlin.time.Duration

/** Semantic representation of a money transfer request */
object Transfer : Durable(
  word(
    "дать"
  ),
  stem(
    "transfer",
    "give",
    "take",
    "get",
    "keep",
    "держи",
    "бери",
    "возьми",
    "трансфер",
    "перевод",
    "дар",
    "подар",
    "взял",
    "забер",
    "забир",
    "перевед",
    "перевест",
    "отправ",
    "send"
  )
) {

  override fun execute(query: Query, duration: Duration): Unit = query.run {
    val recipientMessage =
      message.replyToMessage ?: return
    val sender = message
      .from
      ?.id
      ?: return
    val recipient = recipientMessage
      .from
      ?.id
      ?: return
    storage.transfer(duration, sender, recipient) {
      bot.sendTempMessage(
        message.chat.id,
        "+${duration.print()}",
        replyToMessageId = recipientMessage.messageId,
      )
    }
  }
}