package semnorms

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** Generic type for semantic representations of time */
abstract class Time(vararg rules: Rule) : Semnorm(*rules) {
  abstract fun toDuration(number: Long): Duration
}

object Year : Time(word("y", "yr", "г", "л"), stem("year", "год", "лет")) {
  override fun toDuration(number: Long) = days(number) * 365
}

object Month : Time(stem("mon", "mo", "мес")) {
  override fun toDuration(number: Long) = days(number) * 30
}

object Week : Time(stem("week", "недел")) {
  override fun toDuration(number: Long) = days(number) * 7
}

object Day : Time(word("d", "д"), stem("day", "суток", "сутк", "дня", "ден", "дне")) {
  override fun toDuration(number: Long) = days(number)
}

object Hour : Time(word("h", "ч"), stem("час", "hour")) {
  override fun toDuration(number: Long) = hours(number)
}

object Minute : Time(word("m", "м"), stem("min", "мин")) {
  override fun toDuration(number: Long) = minutes(number)
}

object Second : Time(stem("sec", "сек"), word("s", "с")) {
  override fun toDuration(number: Long) = seconds(number)
}