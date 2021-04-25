package storages

import kotlin.time.Duration

/** Банк времени пользователей */
interface TimeBank {

  /**
   * После регистрации у пользователя начнет копиться время (БОД).
   * Если пользлватель уже зарегистрирован, то ничего не произойдет.
   */
  fun register(account: Long)

  /** Метод для перевода времени со счета на счет */
  fun transfer(
    fromAccount: Long,
    toAccount: Long,
    duration: Duration,
    action: () -> Unit = {}
  )

  /** @return статус счета */
  fun status(account: Long): Duration

  /** Метод для снятия времени со счета */
  fun use(
    account: Long,
    duration: Duration,
    action: (Duration) -> Unit
  )
}