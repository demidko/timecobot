package ml.demidko.timecobot

import co.touchlab.stately.isolate.IsolateState
import com.github.demidko.print.utils.printSeconds
import org.redisson.api.RMap
import org.slf4j.LoggerFactory.getLogger
import java.time.Instant
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

// TODO: стратегия развития этого класса заключается в уходе от
//  DTO к самостоятельной сущности
//  тут инкапуслируется работа с Redis

/**
 * @param storedTime telegram id to seconds count
 * @param pinnedMessages chat id to (messages id to last pinned second)
 * @param restrictedUsers chat id to (admin id to last ban second)
 */
class Storage(
  val storedTime: IsolateState<RMap<Long, Long>>,
  val pinnedMessages: IsolateState<RMap<Long, MutableMap<Long, Long>>>,
  val restrictedUsers: IsolateState<RMap<Long, MutableMap<Long, Long>>>
) {

  private val log = getLogger("Database")

  init {
    /**
     * Each user accumulates time, sec by sec, min by min, hour by hour.
     * This is analogous to the unconditional basic income.
     */
    timer(period = minutes(1).inWholeMilliseconds) {
      storedTime.access {
        for (entry in it.entries) {
          entry.setValue(entry.value + 4)
        }
      }
      log.info("All users are credited with one minute")
    }
  }

  fun muteUser(chat: Long, admin: Long, second: Long) =
    restrictedUsers.access {
      it.getOrPut(chat, ::mutableMapOf).put(admin, second)
    }

  fun isMuted(chat: Long, user: Long): Boolean =
    restrictedUsers.access {
      val admins = it[chat] ?: return@access false
      val untilSeconds = admins[user] ?: return@access false
      if (Instant.now().epochSecond > untilSeconds) {
        admins.remove(user)
        return@access false
      }
      true
    }

  /**
   * After registration, the user begins to accumulate time.
   * If the user is already registered, then nothing will happen.
   */
  fun registerUser(user: Long) = storedTime.access { it.fastPutIfAbsent(user, 60) }

  /** Method of transferring time from account to account */
  inline fun transfer(
    duration: Duration,
    from: Long,
    to: Long,
    crossinline onSuccess: () -> Unit = {}
  ) =
    storedTime.access { timecoins ->
      val sum = duration.inWholeSeconds
      val balance = timecoins[from] ?: error("You don't have time yet.")
      if (sum > balance) {
        error(
          "Not enough time. "
            + "You only have ${balance.printSeconds()}, "
            + "but you need ${sum.printSeconds()} "
        )
      }
      timecoins[from] = balance - sum
      timecoins[to] = (timecoins[to] ?: 0) + sum
      onSuccess()
    }


  /** @return Account balance in whole seconds */
  fun seconds(of: Long) = storedTime.access { it[of] ?: error("You don't have time yet") }

  /** Method of time withdrawal from the account */
  inline fun use(duration: Duration, of: Long, crossinline onSuccess: () -> Unit) =
    storedTime.access { timecoins ->
      val sum = duration.inWholeSeconds
      val balance = timecoins[of] ?: error("You don't have time yet.")
      if (sum > balance) {
        error(
          "Not enough time. "
            + "You only have ${balance.printSeconds()}, "
            + "but you need ${sum.printSeconds()}"
        )
      }
      timecoins[of] = balance - sum
      onSuccess()
    }
}
