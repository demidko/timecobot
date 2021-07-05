package semnorms.commands

import Query
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.User
import semnorms.Executable
import semnorms.stem
import sendTempMessage
import kotlin.time.Duration.Companion.seconds

/** Semantic representation of a faq request */
object Help : Executable(
  stem(
    "помощ",
    "справк",
    "правил",
    "help",
    "rule",
    "faq",
    "start",
    "старт",
  )
) {

  override fun execute(query: Query): Unit = query.run {
    val faq = message.from?.relatedFaq() ?: return@execute
    if (message.chat.id == message.from?.id) {
      bot.sendMessage(ChatId.fromId(message.chat.id), faq, replyToMessageId = message.messageId)
    } else {
      bot.sendTempMessage(
        message.chat.id, faq, replyToMessageId = message.messageId, lifetime = seconds(60)
      )
    }
  }


}

private fun User.relatedFaq() = when {
  isRussian() -> faqRu
  else -> faqEn
}

private fun User.isRussian() = firstName.isRussian() || lastName.isRussian()

private fun String?.isRussian() = this?.lowercase()?.any { it in 'а'..'я' } ?: false

const val faqRu = """
Чтобы начать использовать бота, добавьте его в группу с правами администратора.

1. Время (валюта) начисляется вам автоматически, каждую минуту, совершенно бесплатно и безусловно.
Чтобы узнать свой баланс, напишите в чат запрос, например слово ”баланс”, ”статус” или  ”время”
(В ответе используются сокращения d - дни, h - часы, m - минуты, s - секунды)
   
2. Чтобы заблокировать пользователя используйте приказы вида ”бан 5 минут” или ”блок на 2 часа” в ответном сообщении пользователю.
Бот заблокирует его на указанное время: пользователь останется в чате, но не сможет ничего писать.
   
3 Чтобы выкупить человека из бана просто напишите ”разбань его” или ”выкупить” в ответному сообщении ему.

4. Чтобы перевести время другому человеку напишите, например ”переводи Васе 5 моих минут” в ответном сообщении этому человеку.

5. Чтобы закрепить сообщения за ваше время, напишите что-то вроде ”закрепить” в ответе к сообщению.

Эти приказы можно перформулировать по разному, эксперементируйте!
Остальные вопросы задать можно тут @timecochat
"""

const val faqEn = """
To start using the bot, just add it to the group with admin rights.

1. Time (currency) is credited to you automatically, every minute, completely free and unconditionally.
To check your balance, send a request to the chat, for example word ”balance”, ”status” or ”time” symbol.
Abbreviations: d - days, h - hours, m - minutes, s - seconds

2. To block a person, use requests like ”ban 5 minutes” or ”block for 2 hours” in the reply message to that person.
Bot will block user specified time: user will remain in the chat, but he will not be able to write anything.

3. To unblock a user, simply write to him in the reply message ”unblock”, ”unban”, ”ransom” or ”redeem”.

4. To pass the time to another person, write, for example, ”give my 5 minutes to John” in the reply message to this person.

5. To pin message, write, for example, ”pin” in the reply message to this message.

These orders can be formulated in different ways, experiment!
Still have questions? You can ask them here @timecochat
"""