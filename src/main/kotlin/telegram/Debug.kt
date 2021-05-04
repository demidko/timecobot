import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import org.slf4j.LoggerFactory
import telegram.delayDeleteMessage
import telegram.sendTempMessage

private val log = LoggerFactory.getLogger("Message")

/** Обращение к разработчику */
fun Bot.debug(m: Message) {
  log.info(m.toString())
  sendTempMessage(
    m.chat.id,
    "Developers will think about your message | Мы подумаем над этим 👏",
    replyToMessageId = m.messageId
  )
  delayDeleteMessage(m.chat.id, m.messageId)
}