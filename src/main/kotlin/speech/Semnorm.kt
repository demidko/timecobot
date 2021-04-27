package speech

/** Правило нестрогого соответствия */
private fun startsWithAny(vararg stems: String) = { word: String ->
  stems.any(word::startsWith)
}

/** Правило строгого соответствия */
private fun equalsAny(vararg stems: String) = { word: String ->
  stems.any(word::equals)
}

/** Набор нормализованных семантических представлений. */
sealed class Semnorm(vararg val rules: (String) -> Boolean)

/** Общий тип для семантических представлений времени */
sealed class Time(vararg rules: (String) -> Boolean) : Semnorm(*rules)

object Year : Time(startsWithAny("y", "г", "л"))

object Month : Time(startsWithAny("mon", "мес"))

object Week : Time(startsWithAny("week", "недел"))

object Day : Time(startsWithAny("d", "д"))

object Hour : Time(startsWithAny("h", "ч"))

object Minute : Time(startsWithAny("m", "м"))

object Second : Time(startsWithAny("sec", "сек"), equalsAny("s", "сек"))

object Status : Semnorm(
  startsWithAny(
    "time",
    "врем",
    "balance",
    "status",
    "score",
    "coins",
    "баланс",
    "статус",
    "счет",
    "счёт",
    "узна",
    "!",
    "timecoin"
  )
)

object Transfer : Semnorm(
  startsWithAny(
    "transfer", "give", "take", "get", "keep", "держи", "бери", "возьми",
    "получи", "трансфер", "перевод", "дар", "подар", "взял", "забер", "забир", "перевед", "перевест"
  )
)

object Ban : Semnorm(
  startsWithAny(
    "ban", "block", "freez", "mute", "бан", "блок", "забан",
    "заглох", "умри", "умер"
  )
)

object Redeem : Semnorm(
  startsWithAny(
    "redeem", "unblock", "unban", "unmute", "разбан", "разблок", "ожив", "выкуп", "донат"
  )
)

object Number : Semnorm(startsWithAny("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))

private fun String.matches(norm: Semnorm) = norm.rules.any { it(this) }

/** Список всех распознаваемых семантических норм */
private val semnorms = listOf(
  Year, Month, Week, Day, Hour, Minute, Second,
  Status, Transfer, Ban, Redeem, Number
)

/** Переводим произвольную лексему напр. `забанить` в нормализованное семантическое представление. */
internal val String.semnorm
  get() = semnorms.firstOrNull(toLowerCase()::matches)