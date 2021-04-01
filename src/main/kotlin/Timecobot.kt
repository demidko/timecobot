import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.extensions.filters.Filter
import filters.Ban
import filters.Transfer

fun newTimecobot(owner: String, key: String) = bot {
  token = key
  dispatch {
    message(Transfer) {

    }
    message(Ban) {

    }
  }
}