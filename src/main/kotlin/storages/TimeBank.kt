package storages

import kotlin.time.Duration

/** Интерфейс для управления временем пользователей */
interface TimeBank {

  /** После регистрации у пользователя начнет копиться время */
  fun register(account: Long)

  /** Метод для перевода времени со счета на счет */
  fun transfer(fromAccount: Long, toAccount: Long, duration: Duration)

  /** Метод для снятия времени со счета */
  fun use(account: Long, duration: Duration, action: (Duration) -> Unit)
}