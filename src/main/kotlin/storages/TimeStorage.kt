package storages

import co.touchlab.stately.isolate.IsolateState
import org.slf4j.LoggerFactory.getLogger
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** Thread safe time store for all users */
object TimeStorage {

  private val log = getLogger(javaClass.simpleName)

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
      }
    }
  }

  /**
   * After registration, the user begins to accumulate time.
   * If the user is already registered, then nothing will happen.
   */
  fun register(account: Long) = db.access {
    if (account !in it.keys) {
      it[account] = 0
    }
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
  fun seeTime(account: Long): Duration = db.access {
    seconds(it[account] ?: 0)
  }

  /** Method of time withdrawal from the account */
  fun useTime(account: Long, duration: Duration, action: (Duration) -> Unit) = db.access {
    val balance = it[account] ?: 0
    if (seconds(balance) < duration) {
      error("Not enough money. You only have $balance, but you need $duration")
    }
    it[account] = balance - duration.inWholeSeconds
    action(duration)
  }
}