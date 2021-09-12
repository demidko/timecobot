import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import ml.demidko.timecobot.*
import org.slf4j.LoggerFactory.getLogger
import java.io.Closeable
import java.io.Serializable
import java.lang.Thread.currentThread
import java.time.Instant
import java.time.Instant.now
import java.util.concurrent.Executors.newSingleThreadExecutor
import kotlin.concurrent.thread

/**
 * Класс отвечает за закрепление, продление, и своевременное автоматическое открепление сообщений.
 * Поддерживается многопоточность.
 */
class Promoter(
  private val api: Bot,
  private val bank: Bank,
  private val pinnedMessages: MutableMap<MessageAddress, EpochSecond>
) : Closeable {

  data class MessageAddress(val chatId: ChatId, val messageId: MessageId) : Serializable

  private val log = getLogger("Promoter")

  private val executor = newSingleThreadExecutor()

  /**
   * Запрос на закрепление (!) другого сообщения.
   * Закрепляемое сообщение должно находиться во вложении.
   */
  fun pin(request: Message, to: Seconds) {
    val message = request.replyToMessage ?: return
    if (bank.spend(to, request)) {
      val address = MessageAddress(message.chat.id, message.messageId)
      executor.submit {
        api.pinChatMessage(message.chatId(), message.messageId)
        pinnedMessages[address] = pinnedMessages[address]?.plus(to) ?: now().epochSecond.plus(to)
        log.info(
          "Pin - {} {} before {} (now {})",
          message.text,
          address,
          pinnedMessages[address],
          Instant.now().epochSecond
        )
      }
    }
  }

  /**
   * Запрос на открепление (!) другого сообщения.
   * Открепляемое сообщение должно находиться во вложении.
   */
  fun unpin(request: Message) {
    val message = request.replyToMessage ?: return
    val address = MessageAddress(message.chat.id, message.messageId)
    executor.submit {
      val sec = (pinnedMessages[address] ?: return@submit) - now().epochSecond
      if (sec > 0 && bank.spend(sec, request)) {
        api.unpinChatMessage(message.chatId(), message.messageId)
        pinnedMessages.remove(address)
      }
    }
  }

  override fun close() = executor.shutdown()

  init {
    thread {
      while (!currentThread().isInterrupted) {

        // Собираем ключи устаревших сообщений без очереди ожидания,
        // чтобы не блокировать работу других потоков
        val expired = pinnedMessages.filterValues { it <= now().epochSecond }.keys
        if (expired.isEmpty()) {
          continue
        }

        // Пока мы собирали ключи устаревших сообщений, они могли обновиться,
        // поэтому перед удалением подозреваемых ставим повторную проверку в очередь,
        // последующие задачи остальных потоков в это время будут ждать в SingleThreadExecutor,
        // потому то финальная проверка и будет атомарной и покажет нам точный результат.
        // При этом, так как мы произвели предварительную небезопасную выборку и теперь
        // пройдемся только по ней, то остальные потоки будут ждать нас меньше.
        executor.submit {
          for (address in expired) {
            val epochSecond = pinnedMessages[address]
            if (epochSecond != null && epochSecond <= now().epochSecond) {
              api.unpinChatMessage(address.chatId.telegramId(), address.messageId)
              pinnedMessages.remove(address)
              log.info("Cleanup - {}", address)
            }
          }
        }
      }
    }
  }
}