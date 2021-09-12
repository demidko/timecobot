package ml.demidko.timecobot

import com.github.demidko.print.utils.printSeconds
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import java.io.Closeable
import java.util.concurrent.Executors.newSingleThreadExecutor
import kotlin.concurrent.timer
import kotlin.time.Duration.Companion.minutes

/**
 * Класс отвечает за накопление, получение и перевод средств.
 * Поддерживается многопоточность.
 */
class Bank(
  private val api: Bot,
  private val storedSeconds: MutableMap<UserId, Seconds>
) : Closeable {
  private val executor = newSingleThreadExecutor()

  /**
   * Перевести деньги (!) другому пользователю.
   * Получатель должен находиться во вложенном сообщении
   * @param of отправитель перевода
   */
  fun transfer(money: Seconds, of: Message) {
    val to = of.replyToMessage ?: return
    if (to.from?.id == of.from?.id) {
      return
    }
    if (spend(money, of)) {
      add(money, to)
      api.sendTempMessage(to.chatId(), "+${money.printSeconds()}", replyToMessageId = to.messageId)
    }
  }

  private fun add(money: Seconds, to: Message) {
    val id = to.from?.id ?: return
    executor.submit {
      storedSeconds[id] = storedSeconds[id]?.plus(money) ?: money
    }
  }

  /**
   * Потратить деньги пользователя.
   * @return true если платеж удался
   */
  fun spend(money: Seconds, of: Message): Boolean {
    val user = of.from?.id ?: return false
    val isSuccessfully = executor.submit<Boolean> {
      val availableSeconds = storedSeconds[user]
      if (availableSeconds == null || money > availableSeconds) {
        false
      } else {
        storedSeconds[user] = availableSeconds - money
        true
      }
    }
    if (isSuccessfully.get()) {
      return true
    }
    api.sendTempMessage(of.chatId(), "Not enough money", replyToMessageId = of.messageId)
    return false
  }

  fun inform(m: Message) {
    val id = m.from?.id ?: return
    val report = storedSeconds.getOrDefault(id, 0).printSeconds()
    if (m.from?.id == m.chat.id) {
      api.sendMessage(m.chatId(), report, replyToMessageId = m.messageId)
    } else {
      api.sendTempMessage(m.chatId(), report, replyToMessageId = m.messageId)
    }
  }

  override fun close() = executor.shutdown()

  init {
    timer(period = minutes(1).inWholeMilliseconds) {
      executor.submit {
        for (p in storedSeconds) {
          p.setValue(p.value + 60)
        }
      }
    }
  }
}