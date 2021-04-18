package timeql

/** Набор нормализованных семантических представлений. */
sealed class SemNorm(vararg val stems: String)

sealed class Time(vararg stems: String) : SemNorm(*stems)

object Year : Time("y", "г", "л")

object Month : Time("mon", "мес")

object Week : Time("week", "недел")

object Day : Time("d", "д")

object Hour : Time("h", "ч")

object Minute : Time("m", "м")

object Second : Time("s", "с")

sealed class Action(vararg stems: String) : SemNorm(*stems)

object Status : Action(
  "balance", "status", "score", "coins", "баланс", "статус", "счет", "узнать"
)

sealed class Mutable(vararg stems: String) : Action(*stems)

object Transfer : Mutable(
  "transfer", "give", "take", "get", "keep", "держи", "бери", "возьми",
  "получи", "трансфер", "перевод", "дар", "подар", "взял", "забер", "забир"
)

object Ban : Mutable(
  "ban", "block", "freez", "mute", "бан", "блок", "забан",
  "заглох", "умри", "умер"
)

object Redeem : Mutable(
  "redeem", "unblock", "unban", "unmute", "разбан", "разблок", "ожив", "выкуп", "донат"
)

object Number : SemNorm("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")


private val knownSemNorms = listOf(
  Year, Month, Week, Day, Hour, Minute, Second,
  Status, Transfer, Ban, Redeem, Number
)

/**
 * Переводим набор токенов из контекстно свободной грамматики
 * вида `забанить на 5 минут` в наборы нормализованных семантических представлений.
 */
fun List<String>.withSemNorms() = map { token ->
  val idea = knownSemNorms.firstOrNull {
    it.stems.any { norm ->
      token.toLowerCase().startsWith(norm)
    }
  }
  Pair(token, idea)
}