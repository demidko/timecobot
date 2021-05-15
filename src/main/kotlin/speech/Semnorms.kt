package speech

/** Normalized semantic representation: defined by the rules that apply to lexemes. */
sealed class Semnorm(vararg val rules: (String) -> Boolean)

/** Weak match rule (word's stem) */
private fun stem(vararg stems: String) = { word: String ->
  stems.any(word::startsWith)
}

/** Strict match rule (whole word) */
private fun word(vararg words: String) = { word: String ->
  words.any(word::equals)
}

/** Generic type for semantic representations of time */
sealed class Time(vararg rules: (String) -> Boolean) : Semnorm(*rules)

object Year : Time(word("y", "yr", "г", "л"), stem("year", "год", "лет"))

object Month : Time(stem("mon", "mo", "мес"))

object Week : Time(stem("week", "недел"))

object Day : Time(word("d", "д"), stem("day", "суток", "сутк", "дня", "ден", "дне"))

object Hour : Time(word("h", "ч"), stem("час", "hour"))

object Minute : Time(word("m", "м"), stem("min", "мин"))

object Second : Time(stem("sec", "сек"), word("s", "с"))

/** Semantic representation of balance request */
object Status : Semnorm(
  word("!"),
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
    "timecoin",
    "check"
  )
)

object Pin : Semnorm(stem("закреп", "pin"), word("пин"))

/** Semantic representation of a money transfer request */
object Transfer : Semnorm(
  word(
    "дать"
  ),
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
    "перевест",
    "send"
  )
)

/** Semantic representation of a ban request */
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
    "завали",
    "умри",
    "умер",
    "💥"
  )
)

/** Semantic representation of a redeem request */
object Redeem : Semnorm(
  stem(
    "liberat",
    "heal",
    "ransom",
    "atonement",
    "expiation",
    "redemption",
    "rescue",
    "salvation",
    "redeem",
    "unblock",
    "unban",
    "unmute",
    "разбан",
    "разблок",
    "ожив",
    "выкуп",
    "исцел",
    "искуп",
    "спаст",
    "освобод"
  )
)

/** Semantic representation of a faq request */
object Help : Semnorm(
  stem(
    "помощ",
    "?",
    "справк",
    "правил",
    "помог",
    "help",
    "rule",
    "faq",
    "start",
    "старт",
  )
)

/** Skip telegram-command prefix '/' */
object CommandSymbol : Semnorm(stem("/"))

/** Semantic representation of numbers */
object Number : Semnorm(stem("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"))

/** List of all recognizable semantic norms */
private val semnorms = listOf(

  CommandSymbol,

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
  Pin
)

/**
 * Check if the word matches the semantic norm?
 * Attention! Case sensitive
 */
private fun String.matches(norm: Semnorm) = norm.rules.any { it(this) }

/**
 * We translate an arbitrary token, for example `ban` in normalized semantic representation.
 * Case insensitive.
 */
val String.semnorm get() = semnorms.firstOrNull(lowercase()::matches)