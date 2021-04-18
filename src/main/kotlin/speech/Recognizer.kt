package speech

import Command
import Command.*
import kotlin.time.*

/**
 * Функция распознает команду из произвольного текста
 * на основе наборов нормализованных семантических представлений.
 */
fun String.recognize(): Command {
  val it = tokenize().withSemNorms().iterator()
  val (token, norm) = it.next()
  return when (norm) {
    is Status -> StatusCommand
    is MutableAction -> MutableCommand(norm, it.parseDuration())
    else -> NotAction
  }
}

private fun Iterator<Pair<String, SemNorm?>>.parseDuration(): Duration {
  val (token, norm) = next()
  return when (norm) {
    is Time -> norm.toDuration(parseInt())
    is Number -> parseTime().toDuration(token.toInt())
    else -> parseDuration()
  }
}

private fun Iterator<Pair<String, SemNorm?>>.parseTime(): Time {
  val (_, norm) = next()
  return when (norm) {
    is Time -> norm
    else -> parseTime()
  }
}

private fun Iterator<Pair<String, SemNorm?>>.parseInt(): Int {
  val (token, norm) = next()
  return when (norm) {
    is Number -> token.toInt()
    else -> parseInt()
  }
}

private fun Time.toDuration(number: Int) = when (this) {
  is Second -> number.seconds
  is Minute -> number.minutes
  is Hour -> number.hours
  is Day -> number.days
  is Week -> number.days * 7
  is Month -> number.days * 30
  is Year -> number.days * 365
}