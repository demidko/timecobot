package stats

import co.touchlab.stately.isolate.IsolateState
import com.github.kotlintelegrambot.Bot
import org.slf4j.LoggerFactory.getLogger
import storages.redisSet
import java.io.File
import kotlin.concurrent.timer
import kotlin.time.Duration.Companion.minutes


/**
 * Thread safe group stat store.
 * To use a Redis cluster you need to set the DATABASE_URL environment variable.
 * Otherwise a in-memory database will be used.
 */
object DebugStorage {

  private val log = getLogger(javaClass.simpleName)

  /** Telegram groups ids are elements */
  private val db = IsolateState {
    try {
      redisSet<Long>("timegroups")
    } catch (e: RuntimeException) {
      log.warn(e.message)
      LinkedHashSet()
    }
  }

  fun startDebug(bot: Bot) {
    timer(period = minutes(5).inWholeMilliseconds) {
      db.access { groups ->
        val chats = groups.mapNotNull {
          val (chat, e) = bot.getChat(it)
          if (e != null) {
            log.warn(chat?.raw().toString())
          }
          chat?.body()?.result
        }
        val stats = chats.joinToString(separator = "\n") { chat ->
          val line = StringBuilder(
            "* ${chat.id} — ${chat.title}(${bot.getChatMembersCount(chat.id).first?.body()?.result} members)"
          )
          chat.username?.let {
            line.append(" — https://t.me/$it")
          }
          chat.inviteLink?.let {
            line.append(" — $it")
          }
          bot.getChatAdministrators(chat.id)
            .first
            ?.body()
            ?.result
            ?.mapNotNull { it.user.username }
            ?.map { "@$it" }
            .orEmpty()
            .let {
              line.append(" — admin $it")
            }
          chat.inviteLink
          when (val link = chat.username ?: chat.inviteLink) {
            null -> {
              bot.getChatAdministrators(chat.id)
                .first
                ?.body()
                ?.result
                ?.mapNotNull { it.user.username }
                ?.map { "@$it" }
                .orEmpty()
                .let { line.append("admin$it") }
            }
            else -> {
              line.append(link)
            }
          }
        }
        File("debug.md").apply {
          writeText(stats)
          appendText("\n")
        }
        log.info("${groups.size} connected groups")
      }
    }
  }

  fun debugGroup(id: Long) = db.access { it.add(id) }
}