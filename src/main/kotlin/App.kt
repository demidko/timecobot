import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import java.lang.System.getenv

fun main(args: Array<String>) = bot {
  token = getenv("TOKEN")
  logLevel = Error
  dispatch {
    text {
      bot.sendTempMessage(message.chat.id, "echo $text", replyToMessageId = message.messageId)
    }
  }
}.startPolling()




