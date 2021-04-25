package storages

import co.touchlab.stately.isolate.IsolateState
import org.slf4j.LoggerFactory.getLogger
import kotlin.concurrent.timer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.minutes


object InMemoryTimeBank : TimeBank {

  private val log = getLogger(javaClass.simpleName)

  private val db = IsolateState { LinkedHashMap<Long, Duration>() }

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

  override fun register(account: Long) = db.access {
    if (account !in it.keys) {
      log.info("New telegram id registered: $account")
      it[account] = ZERO
    }
  }

  override fun transfer(
    fromAccount: Long,
    toAccount: Long,
    duration: Duration,
    action: () -> Unit
  ) = use(fromAccount, duration) {
    db.access {
      val balance = it[toAccount] ?: ZERO
      it[toAccount] = balance + duration
    }
    action()
  }


  override fun status(account: Long): Duration = db.access {
    it[account] ?: ZERO
  }


  override fun use(account: Long, duration: Duration, action: (Duration) -> Unit) = db.access {
    val balance = it[account] ?: ZERO
    if (balance < duration) {
      error("Not enough money. You only have $balance, but you need $duration")
    }
    val newBalance = balance - duration
    it[account] = newBalance
    action(newBalance)
  }


  fun <R> seeMap(block: (Map<Long, Duration>) -> R) = db.access(block)
}