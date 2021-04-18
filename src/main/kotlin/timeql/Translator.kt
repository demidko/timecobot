package timeql

import Command
import Command.Mutable
import Command.Status
import kotlin.time.*


/** Пытаемся распознать команды боту из наборов нормализованных семантических представлений. */
fun List<Pair<String, SemNorm>>.parseCommand(): Command {
  val it = iterator()
  val (token, norm) = it.next()
  return when (norm) {
    is timeql.Status -> Status
    is timeql.Mutable -> Mutable(norm, it.parseDuration())
    else -> error("Unrecognized command: $token")
  }
}

fun Iterator<Pair<String, SemNorm>>.parseDuration(): Duration {
  val (token, norm) = next()
  return when (norm) {
    is Time -> norm.toDuration(parseInt())
    is timeql.Number -> parseTime().toDuration(token.toInt())
    else -> parseDuration()
  }
}

fun Iterator<Pair<String, SemNorm>>.parseTime(): Time {
  val (_, norm) = next()
  return when (norm) {
    is Time -> norm
    else -> parseTime()
  }
}

fun Iterator<Pair<String, SemNorm>>.parseInt(): Int {
  val (token, norm) = next()
  return when (norm) {
    is Number -> token.toInt()
    else -> parseInt()
  }
}

fun Time.toDuration(number: Int) = when (this) {
  is Second -> number.seconds
  is Minute -> number.minutes
  is Hour -> number.hours
  is Day -> number.days
  is Week -> number.days * 7
  is Month -> number.days * 30
  is Year -> number.days * 365
}