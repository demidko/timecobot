package storages

import co.touchlab.stately.isolate.IsolateState
import org.slf4j.LoggerFactory.getLogger
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.minutes

class TimeBank {

  private val log = getLogger(javaClass.simpleName)

  private val db = IsolateState { mapBasedStorage() }

  init {
    val settlementPeriod = 1.minutes
    timer(period = settlementPeriod.toLongMilliseconds()) {
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
      it[account] = ZERO
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
      val balance = it[toAccount] ?: ZERO
      it[toAccount] = balance + duration
    }
    action()
  }

  /** @return статус счета */
  fun status(account: Long): Duration = db.access {
    it[account] ?: ZERO
  }

  /** Метод для снятия времени со счета */
  fun use(account: Long, duration: Duration, action: (Duration) -> Unit) = db.access {
    val balance = it[account] ?: ZERO
    if (balance < duration) {
      error("Not enough money. You only have $balance, but you need $duration")
    }
    val newBalance = balance - duration
    it[account] = newBalance
    action(newBalance)
  }
}