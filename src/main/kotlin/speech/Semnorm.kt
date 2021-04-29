package speech

// TODO speech dsl

/** Правило нестрогого соответствия */
private fun start(vararg stems: String) = { word: String ->
  stems.any(word::startsWith)
}

/** Правило строгого соответствия */
private fun eq(vararg stems: String) = { word: String ->
  stems.any(word::equals)
}

/** Набор нормализованных семантических представлений. */
sealed class Semnorm(vararg val rules: (String) -> Boolean)

/** Общий тип для семантических представлений времени */
sealed class Time(vararg rules: (String) -> Boolean) : Semnorm(*rules)

object Year : Time(start("y", "г", "л"))

object Month : Time(start("mon", "мес"))

object Week : Time(start("week", "недел"))

object Day : Time(start("d", "д"))

object Hour : Time(start("h", "ч"))

object Minute : Time(start("m", "м"))

object Second : Time(start("sec", "сек"), eq("s", "с"))

object Status : Semnorm(
  start(
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
    "timecoin",
    "check",
    "start",
  )
)

object Transfer : Semnorm(
  start(
    "transfer",
    "give",
    "take",
    "get",
    "keep",
    "держи",
    "бери",
    "возьми",
    "трансфер",
    "перевод",
    "дар",
    "подар",
    "взял",
    "забер",
    "забир",
    "перевед",
    "перевест"
  )
)

object Ban : Semnorm(
  start(
    "ban",
    "block",
    "freez",
    "mute",
    "бан",
    "блок",
    "забан",
    "заглох",
    "умри",
    "умер"
  )
)

object Redeem : Semnorm(
  start(
    "redeem",
    "unblock",
    "unban",
    "unmute",
    "разбан",
    "разблок",
    "ожив",
    "выкуп",
    "донат"
  )
)

object Help : Semnorm(
  start(
    "помощ",
    "справк",
    "поддержк",
    "админ",
    "модер",
    "правил",
    "помог",
    "?",
    "help",
    "rule",
    "faq",
    "admin",
    "moder",
    "support"
  )
)

object Skip : Semnorm(
  start("/")
)

object Number : Semnorm(start("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))

private fun String.matches(norm: Semnorm) = norm.rules.any { it(this) }

/** Список всех распознаваемых семантических норм */
private val semnorms = listOf(
  Year, Month, Week, Day, Hour, Minute, Second,
  Status, Transfer, Ban, Redeem, Number, Help
)

/** Переводим произвольную лексему напр. `забанить` в нормализованное семантическое представление. */
internal val String.semnorm
  get() = semnorms.firstOrNull(toLowerCase()::matches)