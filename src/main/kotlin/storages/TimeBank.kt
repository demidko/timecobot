package storages

import co.touchlab.stately.isolate.IsolateState
import org.redisson.Redisson.create
import org.slf4j.LoggerFactory.getLogger
import storages.redis.redisClient
import java.lang.System.getenv
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.minutes

class TimeBank {

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
    val settlementPeriod = 1.minutes.toLongMilliseconds()
    timer(period = settlementPeriod) {
      db.access {
        it.entries.forEach { entry ->
          entry.setValue(entry.value + settlementPeriod)
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
      log.info("New telegram user: $account")
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
      it[toAccount] = balance + duration.toLongMilliseconds()
    }
    action()
  }

  /** @return статус счета */
  fun status(account: Long): Duration = db.access {
    (it[account] ?: 0).milliseconds
  }

  /** Метод для снятия времени со счета */
  fun use(account: Long, duration: Duration, action: (Duration) -> Unit) = db.access {
    val balance = it[account] ?: 0
    if (balance.milliseconds < duration) {
      error("Not enough money. You only have $balance, but you need $duration")
    }
    val newBalance = balance - duration.toLongMilliseconds()
    it[account] = newBalance
    action(newBalance.milliseconds)
  }
}