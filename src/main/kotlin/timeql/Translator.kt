package timeql

import Command
import Command.*
import timeql.SemNorm.Action
import timeql.SemNorm.Action.Ban
import timeql.SemNorm.Time.*
import java.util.concurrent.TimeUnit.*
import kotlin.time.Duration

/**
 * Пытаемся распознать набор идей из промежуточного представления
 * в четкую команду по зашитым в коде паттернам
 */
fun List<Pair<String, SemNorm>>.command(): Command {
  val it = iterator()
  if (it.next() !is Action) {
    error("Unexpected idea")
  }
  val xx: (Duration) -> Command = ::Command.Ban

}

fun Iterator<Pair<String, SemNorm>>.parseCommand(): Command = when (val action = next()) {
  Ban -> Ban::class
  SemNorm.Action.Transfer -> Transfer::class
  SemNorm.Action.STATUS -> Command.CheckStatus::class
  else -> error("Unexpected idea")
}



fun Iterator<Pair<String, SemNorm>>.parseDuration(): Duration {
  val token = next()
  return when (token.second) {
    is Second -> SECONDS
    is Minute -> MINUTES
    is Hour -> HOURS
    is Day -> DAYS
    is Week, is Month, is Year -> error("This duration units not supported yet: $token")
    else -> error("Unexpected token: $token")
  }
}

fun Iterator<Pair<String, SemNorm>>.parseNumber() = next().first.toInt()