package telegram

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import kotlin.time.seconds

const val faqRu = """
Я дух чата, похоже вы призвали меня.

1. Время (валюта) начисляется вам автоматически, каждую минуту, совершенно бесплатно и безусловно.
   Чтобы узнать свой баланс, напишите в чат запрос, например слово баланс, статус или знак !
   
2. Чтобы забанить человека используйте приказы вида `бан 5 минут` или `блок на 2 часа` в ответном сообщении человеку.
   Я заблокирую его на указанное вами время: он останется в чате, но не сможет ничего писать.
   
3 Чтобы выкупить человека из бана просто напишите `разбань его` или `выкупить` в ответному сообщении ему.

4. Чтобы перевести время другому человеку напишите, например `переводи Васе 5 моих минут` в ответном сообщении этому человеку.

Я неплохо понимаю русский язык в свободной форме. Эти приказы можно перформулировать по разному, эксперементируйте!
"""

const val faqEn = """
1. Time (currency) is credited to you automatically, every minute, completely free of charge and unconditionally.
   To check your balance, send a request to the chat, for example word balance, status or ! symbol.
   
2. To ban a person, use orders like `ban 5 minutes` or `block for 2 hours` in the reply message to that person.
   I will block him for the time you specified: he will remain in the chat, but he will not be able to write anything.
   
3 To redeem a person from the ban, simply write to him in the reply message `unblock him` or `redeem`.

4. To pass the time to another person, write, for example, `give my 5 minutes to John` in the reply message to this person.

I understand English well. These orders can be formulated in different ways, experiment!
"""


/** Показать справку */
fun Bot.help(m: Message) {
  val user = m.from ?: error("You hasn't telegram id")
  val faq = when (user.firstName.firstOrNull()?.toUpperCase()) {
    in 'А'..'Я' -> faqRu
    else -> faqEn
  }
  sendTempMessage(m.chat.id, faq, replyToMessageId = m.messageId, lifetime = 60.seconds)
  delayDeleteMessage(m.chat.id, m.messageId)
}