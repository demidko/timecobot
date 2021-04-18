package timeql

import java.util.*

/** Промежуточное предсиавление для распознанных примитивных идей в фразе */
@Suppress("EqualsOrHashCode", "LeakingThis")
sealed class Idea(private vararg val stems: String) {

  companion object {

    private val allIdeas = LinkedList<Idea>()

    /**
     * Переводим набор токенов из контекстно свободной грамматики
     * вида `забанить на 5 минут` в промежуточное представление известных нам идей.
     */
    fun List<String>.parse() = map { token ->
      val idea = allIdeas.firstOrNull { it.stems.any(token::startsWith) }
      Pair(token, idea)
    }
  }

  init {
    allIdeas.add(this)
  }

  sealed class ACTION(private vararg val stems: String) : Idea(*stems) {

    object CHECK_STATUS : ACTION(
      "balance", "status", "score", "coins", "баланс", "статус", "счет", "узнать"
    )

    object MAKE_TRANSFER : ACTION(
      "transfer", "give", "take", "get", "keep", "держи", "бери", "возьми",
      "получи", "трансфер", "перевод", "дар", "подар", "взял", "забер", "забир"
    )

    object BAN_USER : ACTION(
      "ban", "block", "freez", "mute", "бан", "блок", "забан",
      "заглох", "умри", "умер"
    )

    object REDEEM_USER : ACTION(
      "redeem", "unblock", "unban", "unmute", "разбан", "разблок", "ожив", "выкуп", "донат"
    )
  }

  object NUMBER : Idea("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")

  sealed class TIME(vararg stems: String) : Idea(*stems) {

    object YEAR : TIME("y", "г", "л")

    object MONTH : TIME("mon", "мес")

    object WEEK : TIME("week", "недел")

    object DAY : TIME("d", "д")

    object HOUR : TIME("h", "ч")

    object MINUTE : TIME("m", "м")

    object SECOND : TIME("s", "с")
  }
}