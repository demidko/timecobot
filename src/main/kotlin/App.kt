import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.text


fun main(args: Array<String>) = bot {
  token = System.getenv("TOKEN")
  dispatch {
    text {

    }
  }
}.startPolling()


