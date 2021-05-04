package storages

import co.touchlab.stately.isolate.IsolateState
import org.redisson.Redisson.create
import org.slf4j.LoggerFactory.getLogger
import java.lang.System.getenv
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** Хранилище времени всех пользователей */
class TimeStorage {

  private val log = getLogger(javaClass.simpleName)

  private val db = IsolateState {
    try {
      getenv("DATABASE_URL")
        .let(::redisClient)
        .let(::create)
        .getMap<Long, Long>("timecoins")
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
   * После регистрации у пользователя начнет копиться время (БОД).
   * Если пользователь уже зарегистрирован, то ничего не произойдет.
   */
  fun register(account: Long) = db.access {
    if (account !in it.keys) {
      it[account] = 0
    }
  }

  /** Метод для перевода времени со счета на счет */
  fun transfer(
    fromAccount: Long,
    toAccount: Long,
    duration: Duration,
    action: () -> Unit = {}
  ) = use(fromAccount, duration) {
    db.access {
      val balance = it[toAccount] ?: 0
      it[toAccount] = balance + duration.inWholeSeconds
    }
    action()
  }

  /** @return статус счета */
  fun status(account: Long): Duration = db.access {
    seconds(it[account] ?: 0)
  }

  /** Метод для снятия времени со счета */
  fun use(account: Long, duration: Duration, action: (Duration) -> Unit) = db.access {
    val balance = it[account] ?: 0
    if (seconds(balance) < duration) {
      error("Not enough money. You only have $balance, but you need $duration")
    }

    it[account] = balance - duration.inWholeSeconds
    action(duration)
  }
}