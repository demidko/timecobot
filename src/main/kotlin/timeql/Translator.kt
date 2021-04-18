package timeql

import Command
import Command.*
import timeql.Idea.ACTION
import timeql.Idea.ACTION.BAN_USER
import java.time.Duration

/**
 * Пытаемся распознать набор идей из промежуточного представления
 * в четкую команду по зашитым в коде паттернам
 */
fun List<Pair<String, Idea>>.command(): Command {
  val it = iterator()
  if (it.next() !is ACTION) {
    error("Unexpected idea")
  }
  val xx: (Duration) -> Command = ::Command.Ban

}

fun Iterator<Pair<String, Idea>>.parseCommand(): Command = when (val action = next()) {
  BAN_USER -> Ban::class
  Idea.ACTION.MAKE_TRANSFER -> Transfer::class
  Idea.ACTION.CHECK_STATUS -> Command.CheckStatus::class
  else -> error("Unexpected idea")
}

fun Iterator<String>.parseDuration(): Duration {

}

fun Iterator<String>.parseDurationUnit() = when(next()) {
  is
}

fun Iterator<Pair<String, Idea>>.parseNumber() = next().toInt()