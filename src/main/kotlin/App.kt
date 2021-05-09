import stats.DebugStorage.startDebug
import telegram.timecobot

fun main() =
  timecobot()
    .also(::startDebug)
    .startPolling()