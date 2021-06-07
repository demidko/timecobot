import co.touchlab.stately.isolate.IsolateState
import kotlin.concurrent.timer
import kotlin.time.Duration

private typealias UserId = Long

private typealias SecondsCount = Long

/**
 * Thread safe time store for all users.
 * (telegram users ids) to (timecoins in whole seconds)
 */
typealias Timecoins = IsolateState<MutableMap<UserId, SecondsCount>>

/**
 * After registration, the user begins to accumulate time.
 * If the user is already registered, then nothing will happen.
 */
fun Timecoins.register(user: UserId) = access {
  it.putIfAbsent(user, 60)
}

/** Method of transferring time from account to account */
inline fun Timecoins.transfer(
  from: UserId,
  to: UserId,
  duration: Duration,
  crossinline onSuccess: () -> Unit = {}
) =
  access { timecoins ->
    val sum = duration.inWholeSeconds
    val balance = timecoins[from] ?: error("You don't have time yet.")
    if (sum > balance) {
      error(
        "Not enough time. "
          + "You only have ${balance.printSeconds()}, "
          + "but you need ${sum.printSeconds()} "
      )
    }
    timecoins[from] = balance - sum
    timecoins[to] = (timecoins[to] ?: 0) + sum
    onSuccess()
  }


/** @return Account balance in whole seconds */
fun Timecoins.seconds(of: UserId) = access { timecoins ->
  timecoins[of] ?: error("You don't have time yet")
}

/** Method of time withdrawal from the account */
inline fun Timecoins.using(account: UserId, duration: Duration, crossinline onSuccess: () -> Unit) =
  access { timecoins ->
    val sum = duration.inWholeSeconds
    val balance = timecoins[account] ?: error("You don't have time yet.")
    if (sum > balance) {
      error(
        "Not enough time. "
          + "You only have ${balance.printSeconds()}, "
          + "but you need ${sum.printSeconds()}"
      )
    }
    timecoins[account] = balance - sum
    onSuccess()
  }

/**
 * Each user accumulates time.
 * This is analogous to the unconditional basic income.
 * @param db user id to balance (in seconds)
 * @param period settlement period (price of one minute)
 */
fun Timecoins.schedulePayments(period: Duration) =
  timer(period = period.inWholeMilliseconds) {
    access {
      println("+++ ...")
      it.replaceAll { _, coins -> coins + 60 }
      println("+++ ok")
    }
  }