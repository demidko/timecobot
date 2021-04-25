import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.logging.LogLevel.Error
import speech.recognize
import storages.InMemoryTimeBank
import java.lang.System.getenv

fun main(args: Array<String>) = bot {

  token = getenv("TOKEN")
  logLevel = Error

  dispatch {

    message {
      InMemoryTimeBank.register(message.from!!.id)
    }

    text {
      val command = text.recognize()
      when(command) {

      }
    }
  }
}.startPolling()




