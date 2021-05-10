import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import speech.*
import telegram.*

fun TextHandlerEnvironment.routeRequest() {
  when (val command = text.parseCommand()) {
    is BanCommand -> bot.ban(command.duration, message)
    is FreeCommand -> bot.free(message)
    is StatusCommand -> bot.status(message)
    is TransferCommand -> bot.transfer(command.duration, message)
    is HelpCommand -> bot.help(message)
  }
}