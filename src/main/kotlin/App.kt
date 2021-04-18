import Command.MutableCommand
import Command.StatusCommand
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.logging.LogLevel.All
import speech.Ban
import speech.Redeem
import speech.Transfer
import speech.recognize
import java.lang.System.getenv
import kotlin.time.Duration


fun main(args: Array<String>) = bot {
  token = getenv("TOKEN")
  logLevel = All()
  dispatch {
    text {
      val command = text.recognize()
      if (command is StatusCommand) {

        bot.status(message.from)
      }
      if (command is MutableCommand) {
        val ()
      }
      when (command) {
        is StatusCommand -> bot.status(message)
        is MutableCommand -> when (command.action) {
          is Transfer -> bot.transfer(message, command.duration)
          is Redeem -> bot.redeem(message, command.duration)
          is Ban -> bot.ban(message, command.duration)
        }
      }
    }
  }
}.startPolling()

fun Bot.status(m: Message) {

}

fun Bot.ban(m: Message, duration: Duration) {
  val (sender, recipient) = m.senderAndRecipient
}

fun Bot.transfer(m: Message, duration: Duration) {
  val (sender, recipient) = m.senderAndRecipient
}

fun Bot.redeem(m: Message, duration: Duration) {
  val (sender, recipient) = m.senderAndRecipient
}

val Message.sender get() = from?.id!!

val Message.senderAndRecipient get() = Pair(from?.id!!, replyToMessage?.from?.id!!)


