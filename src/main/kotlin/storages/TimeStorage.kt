package storages

import co.touchlab.stately.isolate.IsolateState
import org.slf4j.LoggerFactory.getLogger
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/**
 * Thread safe time store for all users.
 * To use a Redis cluster you need to set the DATABASE_URL environment variable.
 * Otherwise a in-memory database will be used.
 */
object TimeStorage {

  private val log = getLogger(javaClass.simpleName)

  /**
   * Telegram users ids are keys
   * Timecoins in whole seconds are values
   */
  private val db = IsolateState {
    try {
      redisMap<Long, Long>("timecoins")
    } catch (e: RuntimeException) {
      log.warn(e.message)
      LinkedHashMap()
    }
  }

  init {
    val settlementPeriod = minutes(1)
    timer(period = settlementPeriod.inWholeMilliseconds) {
      db.access {
        for (entry in it.entries) {
          entry.setValue(entry.value + settlementPeriod.inWholeSeconds)
        }
        log.info("${it.size} connected users")
      }
    }
  }

  /**
   * After registration, the user begins to accumulate time.
   * If the user is already registered, then nothing will happen.
   */
  fun registerUser(id: Long) = db.access {
    it.putIfAbsent(id, 0)
  }

  /** Method of transferring time from account to account */
  fun transferTime(
    fromAccount: Long,
    toAccount: Long,
    duration: Duration,
    action: () -> Unit = {}
  ) = useTime(fromAccount, duration) {
    db.access {
      val balance = it[toAccount] ?: 0
      it[toAccount] = balance + duration.inWholeSeconds
    }
    action()
  }

  /** @return Account status */
  fun seeTime(account: Long) = db.access {
    seconds(it[account] ?: 0)
  }

  /** Method of time withdrawal from the account */
  fun useTime(account: Long, duration: Duration, action: (Duration) -> Unit) = db.access {
    val balance = seconds(it[account] ?: 0)
    if (balance < duration) {
      error("Not enough time. You only have $balance, but you need $duration")
    }
    it[account] = balance.inWholeSeconds - duration.inWholeSeconds
    action(duration)
  }
}