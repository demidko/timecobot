package storages

import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO


object InMemoryTimecoinStorage : TimecoinStorage {

  init {
    // тут накручиваем "зп" каждую минуту всем подряд
  }

  private val db = LinkedHashMap<Long, Duration>()

  override fun register(account: Long) {
    TODO("Not yet implemented")
  }

  override fun topUp(account: Long, duration: Duration) {
    if (duration > ZERO) {
      db[account] = db.getOrDefault(account, ZERO) + duration
    }
  }

  override fun use(account: Long, duration: Duration, action: (Duration) -> Unit) {
    val previousBalance = db.getOrDefault(account, ZERO)
    if (previousBalance < duration) {
      error("Not enough money. You only have $previousBalance, but you need $duration")
    }
    val newBalance = previousBalance - duration
    db[account] = newBalance
    action(newBalance)
  }
}