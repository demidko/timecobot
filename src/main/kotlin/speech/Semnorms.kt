package speech

/** Набор нормализованных семантических представлений. */
sealed class Semnorm(vararg val stems: String)

/** Общий тип для семантических представлений времени */
sealed class Time(vararg stems: String) : Semnorm(*stems)

object Year : Time("y", "г", "л")

object Month : Time("mon", "мес")

object Week : Time("week", "недел")

object Day : Time("d", "д")

object Hour : Time("h", "ч")

object Minute : Time("m", "м")

object Second : Time("s", "с")

object Status : Semnorm(
  "balance", "status", "score", "coins", "баланс", "статус", "счет", "узнать", "!", "timecoin"
)

object Transfer : Semnorm(
  "transfer", "give", "take", "get", "keep", "держи", "бери", "возьми",
  "получи", "трансфер", "перевод", "дар", "подар", "взял", "забер", "забир", "перевед", "перевест"
)

object Ban : Semnorm(
  "ban", "block", "freez", "mute", "бан", "блок", "забан",
  "заглох", "умри", "умер"
)

object Redeem : Semnorm(
  "redeem", "unblock", "unban", "unmute", "разбан", "разблок", "ожив", "выкуп", "донат"
)

object Number : Semnorm("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

/** Список всех распознаваемых семантических норм */
private val semnorms = listOf(
  Year, Month, Week, Day, Hour, Minute, Second,
  Status, Transfer, Ban, Redeem, Number
)

/** Переводим произвольную лексему напр. `забанить` в нормализованное семантическое представление. */
internal fun findSemnorm(lexeme: String) = semnorms.firstOrNull {
  it.stems.any(lexeme.toLowerCase()::startsWith)
}