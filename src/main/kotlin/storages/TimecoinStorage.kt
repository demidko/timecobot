package storages

import kotlin.time.Duration


interface TimecoinStorage {

  fun register(account: Long)

  /** Пополнить счет */
  fun topUp(account: Long, duration: Duration)

  /** Использовать заданную сумму со счета */
  fun use(account: Long, duration: Duration, action: (Duration) -> Unit)
}