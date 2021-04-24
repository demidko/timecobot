import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message

fun Bot.useBanhammer(reason: Message) {
  val user = reason.replyToMessage?.from?.id ?: error("You need to reply to the user to ban him")
  TODO()
}