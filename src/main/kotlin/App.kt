import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import features.*
import org.slf4j.LoggerFactory.getLogger
import speech.*
import storages.TimeStorage
import utils.MissedReplyException
import utils.notifyUserAbout
import java.lang.System.currentTimeMillis
import java.lang.System.getenv

fun main() = bot {
  val log = getLogger("Bot")
  token = getenv("TOKEN")
  logLevel = Error
  dispatch {
    text {
      val timestamp = currentTimeMillis()
      try {
        message.from?.id?.let(TimeStorage::registerUser)
        when (val command = text.command()) {
          is BanCommand -> bot.ban(command.duration, message)
          is FreeCommand -> bot.unban(message)
          is StatusCommand -> bot.balance(message)
          is TransferCommand -> bot.transfer(command.duration, message)
          is HelpCommand -> bot.help(message)
        }
      } catch (e: MissedReplyException) {
        notifyUserAbout(e)
        log.warn("${e.message}: $text")
      } catch (e: RuntimeException) {
        notifyUserAbout(e)
        log.error(text, e)
      } finally {
        val elapsedMs = currentTimeMillis() - timestamp
        if (elapsedMs > 75) {
          log.warn("Too large message processed ${elapsedMs}ms: $text")
        }
      }
    }
  }
}.startPolling()