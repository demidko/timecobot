package features

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.User
import utils.sendTempMessage
import kotlin.time.Duration.Companion.seconds

const val faqRu = """
Чтобы начать использовать бота, добавьте его в группу с правами администратора.

1. Время (валюта) начисляется вам автоматически, каждую минуту, совершенно бесплатно и безусловно.
Чтобы узнать свой баланс, напишите в чат запрос, например слово ”баланс”, ”статус” или знак ”!”
(В ответе используются сокращения d - дни, h - часы, m - минуты, s - секунды)
   
2. Чтобы заблокировать человека используйте приказы вида ”бан 5 минут” или ”блок на 2 часа” в ответном сообщении человеку.
Я заблокирую его на указанное вами время: он останется в чате, но не сможет ничего писать.
   
3 Чтобы выкупить человека из бана просто напишите ”разбань его” или ”выкупить” в ответному сообщении ему.

4. Чтобы перевести время другому человеку напишите, например ”переводи Васе 5 моих минут” в ответном сообщении этому человеку.

5. Чтобы закрепить сообщения за ваше время, напишите что-то вроде ”закрепить на 2 часа” в ответе к сообщению.

Бот неплохо понимает русский в свободной форме. Эти приказы можно перформулировать по разному, эксперементируйте!

Остальные вопросы задать можно тут @timecochat
"""

const val faqEn = """
To start using the bot, just add it to the group with admin rights.

1. Time (currency) is credited to you automatically, every minute, completely free of charge and unconditionally.
To check your balance, send a request to the chat, for example word ”balance”, ”status” or ”!” symbol.
(The answer uses abbreviations, d - days, h - hours, m - minutes, s - seconds)
   
2. To ban a person, use orders like ”ban 5 minutes” or ”block for 2 hours” in the reply message to that person.
I will block him for the time you specified: he will remain in the chat, but he will not be able to write anything.
   
3. To ransom a person from the ban, simply write to him in the reply message ”unblock”, ”unban”, ”ransom” or ”redeem”.

4. To pass the time to another person, write, for example, ”give my 5 minutes to John” in the reply message to this person.

5. To pin message, write, for example, ”pin for 2h” in the reply message to this message.

Еру bot understand English well. These orders can be formulated in different ways, experiment!

Still have questions? You can ask them here @timecochat
"""

/** faq */
fun Bot.help(m: Message) {
  val faq = m.from?.relatedFaq ?: error("You hasn't telegram id")
  if (m.chat.id == m.from?.id) {
    sendMessage(ChatId.fromId(m.chat.id), faq, replyToMessageId = m.messageId)
  } else {
    sendTempMessage(m.chat.id, faq, replyToMessageId = m.messageId, lifetime = seconds(60))
  }
}

private val User.relatedFaq
  get() = when {
    isRussian -> faqRu
    else -> faqEn
  }

private val User.isRussian
  get() =
    (firstName + lastName).lowercase().any { it in 'а'..'я' }