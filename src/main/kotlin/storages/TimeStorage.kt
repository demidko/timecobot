package storages

import co.touchlab.stately.isolate.IsolateState
import org.redisson.Redisson
import org.slf4j.LoggerFactory.getLogger
import toHumanView
import java.lang.System.getenv
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * Thread safe time store for all users.
 * To use a Redis cluster you need to set the DATABASE_URL environment variable.
 * Otherwise a in-memory database will be used.
 */
object TimeStorage {

  /**
   * Telegram users ids are keys
   * Timecoins in whole seconds are values
   */
  private val db = IsolateState {
    try {
      getenv("DATABASE_URL")
        .let(::redisConfig)
        .let(Redisson::create)
        .getMap<Long, Long>("timecoins")
    } catch (e: RuntimeException) {
      getLogger(javaClass.simpleName).warn("${e.message}. In-memory database will be used")
      LinkedHashMap()
    }
  }

  /**
   * Each user accumulates time.
   * This is analogous to the unconditional basic income.
   * Minute by minute, hour by hour.
   */
  init {
    val settlementPeriod = minutes(1)
    val settlementPeriodSec = settlementPeriod.inWholeSeconds
    timer(period = settlementPeriod.inWholeMilliseconds) {
      db.access {
        for (user in it.entries) {
          user.setValue(user.value + settlementPeriodSec)
        }
      }
    }
  }

  /**
   * After registration, the user begins to accumulate time.
   * If the user is already registered, then nothing will happen.
   */
  fun registerUser(id: Long) = db.access { it.putIfAbsent(id, 60) }

  /** Method of transferring time from account to account */
  fun transferTime(
    fromAccount: Long,
    toAccount: Long,
    duration: Duration,
    action: () -> Unit = {}
  ) = db.access { timecoins ->
    val sum = duration.inWholeSeconds
    val balance = timecoins[fromAccount] ?: error("You don't have time yet.")
    if (sum > balance) {
      error(
        "Not enough time. "
          + "You only have ${balance.toHumanView()}, "
          + "but you need ${sum.toHumanView()} "
      )
    }
    timecoins[fromAccount] = balance - sum
    timecoins[toAccount] = (timecoins[toAccount] ?: 0) + sum
    action()
  }


  /** @return Account status */
  fun seeTimeInWholeSeconds(account: Long) = db.access { timecoins ->
    timecoins[account] ?: error("You don't have time yet")
  }

  /** Method of time withdrawal from the account */
  fun useTime(account: Long, duration: Duration, action: () -> Unit) = db.access { timecoins ->
    val sum = duration.inWholeSeconds
    val balance = timecoins[account] ?: error("You don't have time yet.")
    if (sum > balance) {
      error(
        "Not enough time. "
          + "You only have ${balance.toHumanView()}, "
          + "but you need ${sum.toHumanView()}"
      )
    }
    timecoins[account] = balance - sum
    action()
  }
}

