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
    timer(period = minutes(1).inWholeMilliseconds) {
      db.access { groups ->
        val chats = groups.mapNotNull {
          bot.getChat(it)
            .first
            ?.body()
            ?.result
        }
        val stats = chats.joinToString(separator = "\n") { chat ->
          val lineBuilder = StringBuilder("* ${chat.id}")
          chat.firstName?.let {
            lineBuilder.append(" — ").append(it)
          }
          chat.lastName?.let {
            lineBuilder.append(" ").append(it)
          }
          lineBuilder.append(" — ")
          when (val link = chat.username ?: chat.inviteLink) {
            null -> {
              bot.getChatAdministrators(chat.id)
                .first
                ?.body()
                ?.result
                ?.mapNotNull { it.user.username }
                .orEmpty()
                .let(lineBuilder::append)
            }
            else -> {
              lineBuilder.append(link)
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