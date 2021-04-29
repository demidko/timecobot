package speech

/** Нормализованное семантическое представление: определяется правилами применяемыми к лексемам */
sealed class Semnorm(vararg val rules: (String) -> Boolean)

/** Правило нестрогого соответствия (стем) */
private fun stem(vararg stems: String) = { word: String ->
  stems.any(word::startsWith)
}

/** Правило строгого соответствия (слово) */
private fun word(vararg words: String) = { word: String ->
  words.any(word::equals)
}

/** Общий тип для семантических представлений времени */
sealed class Time(vararg rules: (String) -> Boolean) : Semnorm(*rules)

object Year : Time(stem("y", "г", "л"))

object Month : Time(stem("mon", "мес"))

object Week : Time(stem("week", "недел"))

object Day : Time(stem("d", "д"))

object Hour : Time(word("h", "ч"), stem("час", "hour"))

object Minute : Time(stem("m", "м"))

object Second : Time(stem("sec", "сек"), word("s", "с"))

/** Семантическое представление запроса баланса */
object Status : Semnorm(
  stem(
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

/** Семантическое представление запроса перевода средств */
object Transfer : Semnorm(
  stem(
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

/** Семантическое представление бана */
object Ban : Semnorm(
  stem(
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

/** Семантическое представление выкупа */
object Redeem : Semnorm(
  stem(
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

/** Семантическое представление просьбы о помощи */
object Help : Semnorm(
  stem(
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

/** Семантическое представление обращений к разработчику */
object Debug : Semnorm(
  stem(
    "разраб",
    "dev",
    "создатель",
    "creator",
    "bot",
    "программист",
    "programmer",
    "бот",
    "автор",
    "author"
  )
)

/** Пропускаем семантическое представление префикса telegram-команд */
object Skip : Semnorm(stem("/"))

/** Семантическое представление чисел */
object Number : Semnorm(stem("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))

/** Список всех распознаваемых семантических норм */
private val semnorms = listOf(

  Number,

  Year,
  Month,
  Week,
  Day,
  Hour,
  Minute,
  Second,

  Status,
  Transfer,
  Ban,
  Redeem,
  Help,
  Debug
)

/**
 * Проверка, совпадает ли слово с семантической нормой?
 * Внимание! Учитывается регистр
 * @return true если совпадает и false если нет
 */
private fun String.matches(norm: Semnorm) = norm.rules.any { it(this) }

/**
 * Переводим произвольную лексему напр. `забанить` в нормализованное семантическое представление.
 * Регистр не учитывается.
 */
val String.semnorm get() = semnorms.firstOrNull(toLowerCase()::matches)