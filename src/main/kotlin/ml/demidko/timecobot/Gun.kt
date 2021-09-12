import com.github.demidko.print.utils.printSeconds
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.ChatPermissions
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.entities.ParseMode.MARKDOWN_V2
import ml.demidko.timecobot.*
import java.io.Closeable
import java.io.Serializable
import java.lang.Thread.currentThread
import java.time.Instant.now
import java.util.concurrent.Executors.newSingleThreadExecutor
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.days

/**
 * Класс отвечает за блокировки и разблокировки пользователей.
 * Поддерживается многопоточность.
 */
class Gun(
  private val api: Bot,
  private val bank: Bank,
  private val mutedUsers: MutableMap<UserAddress, EpochSecond>
) : Closeable {

  data class UserAddress(val chatId: ChatId, val userId: UserId) : Serializable

  private companion object {

    private const val MIN_BAN = 30L

    private val MAX_BAN = days(365).inWholeSeconds

    private val NO_PERMISSIONS = ChatPermissions(
      canSendMessages = false,
      canSendMediaMessages = false,
      canSendPolls = false,
      canSendOtherMessages = false,
      canAddWebPagePreviews = false,
      canChangeInfo = false,
      canPinMessages = false,
      canInviteUsers = false,
    )

    private val ALL_PERMISSIONS = ChatPermissions(
      canSendMessages = true,
      canSendMediaMessages = true,
      canSendPolls = true,
      canSendOtherMessages = true,
      canAddWebPagePreviews = true,
      canChangeInfo = true,
      canPinMessages = true,
      canInviteUsers = true,
    )
  }

  private val executor = newSingleThreadExecutor()

  /**
   * Запрос на блокировку (!) другого пользователя.
   * Блокируемый пользователь должен находиться во вложенном сообщении.
   * @param request запрос от стрелка
   */
  fun mute(request: Message, to: Seconds) {
    val reply = request.replyToMessage ?: return
    val victimId = reply.from?.id ?: return
    @Suppress("NAME_SHADOWING") val to = when {
      to < MIN_BAN -> MIN_BAN
      to > MAX_BAN -> MAX_BAN
      else -> to
    }
    if (bank.spend(to, request)) {
      val address = UserAddress(reply.chat.id, victimId)
      val chatId = reply.chatId()
      executor.submit {
        val mutedBefore = mutedUsers[address]?.plus(to) ?: now().epochSecond.plus(to)
        mutedUsers[address] = mutedBefore
        api.restrictChatMember(chatId, victimId, NO_PERMISSIONS, mutedBefore)
      }
      api.sendTempMessage(chatId, "💥", replyToMessageId = reply.messageId)
      api.sendMessage(
        chatId,
        "[${request.from?.fullName}](tg://user?id=${request.from!!.id}) muted your for ${to.printSeconds()}",
        parseMode = MARKDOWN_V2,
        replyToMessageId = reply.messageId
      )
    }
  }

  /**
   * Проверяет не заблокирован ли отправитель сообщения. Если да, то удаляет его и возвращает true
   */
  fun muted(m: Message): Boolean {
    val address = UserAddress(m.chat.id, m.from?.id ?: return false)
    val mutedBefore = mutedUsers[address] ?: return false
    val duration = mutedBefore - now().epochSecond
    if (duration > 0) {
      api.deleteMessage(m.chatId(), m.messageId)
      val savedMessage = m.text ?: m.toString()
      api.sendMessage(
        address.userId.telegramId(),
        "`$savedMessage`\nhas been deleted because " +
          "you were muted for ${duration.printSeconds()} in chat \n${m.chat}",
        parseMode = MARKDOWN_V2
      )
      return true
    }
    return false
  }

  /**
   * Запрос от пользователя на разблокировку (!) другого пользователя.
   * Пользователь которого нужно разблокировать, должен находиться во вложенном сообщении.
   * @param request запрос от доброго самаритянина
   */
  fun unmute(request: Message) {
    val reply = request.replyToMessage ?: return
    val address = UserAddress(reply.chat.id, reply.from?.id ?: return)
    val chatId = reply.chatId()
    executor.submit {
      val mutedBefore = mutedUsers[address] ?: return@submit
      val duration = mutedBefore - now().epochSecond
      if (bank.spend(duration, request)) {
        api.restrictChatMember(chatId, address.userId, ALL_PERMISSIONS)
        api.sendTempMessage(chatId, "You are free now!", replyToMessageId = reply.messageId)
        mutedUsers.remove(address)
      }
    }
  }

  override fun close() = executor.shutdown()

  init {
    thread {
      while (!currentThread().isInterrupted) {
        // Собираем ключи устаревших сообщений без очереди ожидания,
        // чтобы не блокировать работу других потоков
        val expired = mutedUsers.filterValues { it <= now().epochSecond }.keys
        if (expired.isEmpty()) {
          continue
        }
        // Пока мы собирали ключи устаревших записей, они могли обновиться,
        // поэтому перед удалением подозреваемых ставим повторную проверку в очередь,
        // последующие задачи остальных потоков в это время будут ждать в SingleThreadExecutor,
        // потому то финальная проверка и будет атомарной и покажет нам точный результат.
        // При этом, так как мы произвели предварительную небезопасную выборку и теперь
        // пройдемся только по ней, то остальные потоки будут ждать нас меньше.
        executor.submit {
          for (address in expired) {
            val epochSecond = mutedUsers[address]
            if (epochSecond != null && epochSecond <= now().epochSecond) {
              mutedUsers.remove(address)
            }
          }
        }
      }
    }
  }
}