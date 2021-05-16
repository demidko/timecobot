package utils

import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.entities.ChatId
import org.slf4j.LoggerFactory.getLogger

private val log = getLogger("Chat")

fun MessageHandlerEnvironment.handleChat() {
  if (message.chat.type in listOf("group", "supergroup")) {
    message.chat.apply {
      if (username == null && inviteLink == null) {
        bot.getChat(ChatId.fromId(message.chat.id)).getOrNull()?.run {
          log.info(
            "${message.text ?: ""}\n{\n" +
              "  $title $firstName $lastName\n" +
              "  $bio $description\n" +
              "  @$username\n" +
              "  $inviteLink\n" +
              "}"
          )
        }
      } else {
        log.info(
          "${message.text ?: ""}\n{\n" +
            "  $title $firstName $lastName\n" +
            "  $bio $description\n" +
            "  @$username\n" +
            "  $inviteLink\n" +
            "}"
        )
      }
    }
  }
}