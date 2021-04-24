package storages

import kotlin.time.Duration

abstract class TimecoinsStorage {

  abstract fun startPeriodicCharges(telegramId: Long)

  abstract fun topUp(account: Long, duration: Duration)

  abstract fun use(account: Long, duration: Duration, action: (Duration) -> Unit)
}