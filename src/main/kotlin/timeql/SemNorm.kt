package timeql

import java.util.*

/** Набор нормализованных семантических представлений. */
@Suppress("EqualsOrHashCode", "LeakingThis")
sealed class SemNorm(private vararg val stems: String) {

  companion object {

    private val norms = LinkedList<SemNorm>()

    /**
     * Переводим набор токенов из контекстно свободной грамматики
     * вида `забанить на 5 минут` в наборы нормализованных семантических представлений.
     */
    fun List<String>.parse() = map { token ->
      val idea = norms.firstOrNull { it.stems.any(token::startsWith) }
      Pair(token, idea)
    }
  }

  init {
    norms.add(this)
  }

  sealed class Action(private vararg val stems: String) : SemNorm(*stems) {

    object Status : Action(
      "balance", "status", "score", "coins", "баланс", "статус", "счет", "узнать"
    )

    object Transfer : Action(
      "transfer", "give", "take", "get", "keep", "держи", "бери", "возьми",
      "получи", "трансфер", "перевод", "дар", "подар", "взял", "забер", "забир"
    )

    object Ban : Action(
      "ban", "block", "freez", "mute", "бан", "блок", "забан",
      "заглох", "умри", "умер"
    )

    object Redeem : Action(
      "redeem", "unblock", "unban", "unmute", "разбан", "разблок", "ожив", "выкуп", "донат"
    )
  }

  object Number : SemNorm("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

  sealed class Time(vararg stems: String) : SemNorm(*stems) {

    object Year : Time("y", "г", "л")

    object Month : Time("mon", "мес")

    object Week : Time("week", "недел")

    object Day : Time("d", "д")

    object Hour : Time("h", "ч")

    object Minute : Time("m", "м")

    object Second : Time("s", "с")
  }
}