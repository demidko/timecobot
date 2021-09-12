/**
 * Тут мы просто определяем читаемые псевдонимы и расширяем библиотечные API.
 */
package ml.demidko.timecobot

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.*
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ChatId.Companion.fromId
import org.redisson.Redisson.create
import org.redisson.config.Config
import java.util.*
import kotlin.concurrent.schedule

private val TMP_MESSAGES_TIMER = Timer()

fun Long.telegramId() = fromId(this)

fun Message.chatId() = fromId(chat.id)

typealias ChatId = Long

typealias MessageId = Long

typealias EpochSecond = Long

typealias UserId = Long

typealias Seconds = Long

/**
 * Use this method to send text messages
 * @param chatId Unique identifier for the target chat or username of the target channel (in the format @channelusername)
 * @param text text of the message to be sent, 1-4096 characters after entities parsing
 * @param parseMode mode for parsing entities in the message text
 * @param disableWebPagePreview disables link previews for links in this message
 * @param disableNotification sends the message silently - users will receive a notification with no sound
 * @param replyToMessageId if the message is a reply, ID of the original message
 * @param replyMarkup additional options - inline keyboard, custom reply keyboard, instructions to remove reply
 * keyboard or to force a reply from the user
 * @return the sent [Message] on success
 */
fun Bot.sendTempMessage(
  chatId: ChatId,
  text: String,
  parseMode: ParseMode? = null,
  disableWebPagePreview: Boolean? = null,
  disableNotification: Boolean? = null,
  replyToMessageId: Long? = null,
  allowSendingWithoutReply: Boolean? = null,
  replyMarkup: ReplyMarkup? = null
) {
  val messageId = sendMessage(
    chatId,
    text,
    parseMode,
    disableWebPagePreview,
    disableNotification,
    replyToMessageId,
    allowSendingWithoutReply,
    replyMarkup
  ).first
    ?.body()
    ?.result
    ?.messageId
    ?: error("Failed to send message")
  delayDeleteMessage(chatId, messageId)
}

/**
 * Use this method to delete a message, including service messages, with the following limitations:
 * - A message can only be deleted if it was sent less than 48 hours ago.
 * - A dice message in a private chat can only be deleted if it was sent more than 24 hours ago.
 * - Bots can delete outgoing messages in private chats, groups, and supergroups.
 * - Bots can delete incoming messages in private chats.
 * - Bots granted `can_post_messages` permissions can delete outgoing messages in channels.
 * - If the bot is an administrator of a group, it can delete any message there.
 * - If the bot has `can_delete_messages` permission in a supergroup or a channel, it can delete any message there.
 *
 * @param chatId Unique identifier for the target chat or username of the target channel (in
 * the format @channelusername)
 * @param messageId Identifier of the message to delete.
 *
 * @return True on success.
 */
fun Bot.delayDeleteMessage(
  chatId: ChatId,
  messageId: Long,
  delay: Seconds = 15
) = TMP_MESSAGES_TIMER.schedule((delay * 1000).toLong()) {
  deleteMessage(chatId, messageId)
}

fun redisOf(connection: String) = create(Config().apply {
  useSingleServer().apply {
    val authorizationIdx = 9
    username =
      connection
        .substring(authorizationIdx)
        .substringBefore(':')
    password =
      connection
        .substring(authorizationIdx + username.length + 1)
        .substringBefore('@')
    address = connection
  }
})

private fun User.relatedFaq() = when {
  isRussian() -> faqRu
  else -> faqEn
}

fun Bot.faq(m: Message) {
  val faq = m.from?.relatedFaq() ?: return
  sendTempMessage(m.chatId(), faq, replyToMessageId = m.messageId)
}

fun User.isRussian() = firstName.isRussian() || lastName.isRussian()

fun String?.isRussian() = this?.lowercase()?.any { it in 'а'..'я' } ?: false

private val faqRu =
  """
    Чтобы начать использовать бота, добавьте его в группу с правами администратора.
    1. Время (валюта) начисляется автоматически по секретной формуле.
    Чтобы узнать свой баланс, напишите в чат запрос, например слово ”баланс”, ”статус” или  ”время”
    (В ответе используются сокращения d - дни, h - часы, m - минуты, s - секунды) 
    2. Чтобы заблокировать пользователя используйте приказы вида ”бан 5 минут” или ”блок на 2 часа” в ответном сообщении пользователю.
    Бот заблокирует его на указанное время: пользователь останется в чате, но не сможет ничего писать.
    3 Чтобы выкупить человека из бана просто напишите ”разбань его” или ”выкупить” в ответному сообщении ему.
    4. Чтобы перевести время другому человеку напишите, например ”переводи Васе 5 моих минут” в ответном сообщении этому человеку.
    5. Чтобы закрепить сообщения за ваше время, напишите что-то вроде ”закрепить” в ответе к сообщению.
    Эти приказы можно перeформулировать по разному, эксперементируйте!
    Остальные вопросы задать можно тут @timecochat
  """.trimIndent()

private val faqEn =
  """
    To start using the bot, just add it to the group with admin rights.
    1. Time (currency) is calculated automatically using a secret formula.
    To check your balance, send a request to the chat, for example word ”balance”, ”status” or ”time” symbol.
    Abbreviations: d - days, h - hours, m - minutes, s - seconds
    2. To block a person, use requests like ”ban 5 minutes” or ”block for 2 hours” in the reply message to that person.
    Bot will block user specified time: user will remain in the chat, but he will not be able to write anything.
    3. To unblock a user, simply write to him in the reply message ”unblock”, ”unban”, ”ransom” or ”redeem”.
    4. To pass the time to another person, write, for example, ”give my 5 minutes to John” in the reply message to this person.
    5. To pin message, write, for example, ”pin” in the reply message to this message.
    These orders can be formulated in different ways, experiment!
    Still have questions? You can ask them here @timecochat
  """.trimIndent()

val User.fullName: String
  get() {
    return when (lastName) {
      null -> firstName
      else -> "$firstName $lastName"
    }
  }
