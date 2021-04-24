package timecoin

object InMemoryTimecoinStorage : TimecoinStorage {

  private val db = LinkedHashMap<Long, Long>()

  override fun topUpBalance(account: Long, amount: Long) {
    if (amount > 0) {
      db[account] = db.getOrDefault(account, 0) + amount
    }
  }

  override fun useBalance(account: Long, amount: Long, action: (Long) -> Unit) {
    val previousBalance = db.getOrDefault(account, 0)
    if (previousBalance < amount) {
      error("Not enough money. You only have $previousBalance$, but you need $amount$")
    }
    val newBalance = previousBalance - amount
    db[account] = newBalance
    action(newBalance)
  }
}