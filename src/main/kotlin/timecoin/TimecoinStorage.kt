package timecoin


interface TimecoinStorage {

  /** Пополнить счет */
  fun topUpBalance(account: Long, amount: Long)

  /** Использовать заданную сумму со счета */
  fun useBalance(account: Long, amount: Long, action: Long.() -> Unit)
}