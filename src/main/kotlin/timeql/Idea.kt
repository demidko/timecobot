package timeql

import java.util.*

/** Промежуточное предсиавление для распознанных примитивных идей в фразе */
@Suppress("EqualsOrHashCode", "LeakingThis")
sealed class Idea(private vararg val stems: String) {

  init {
    allIdeas.add(this)
  }

  companion object {
    private val allIdeas = LinkedList<Idea>()

    fun recognizeIdeaOrNull(token: String) = allIdeas.firstOrNull {
      it.stems.any(token::startsWith)
    }
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

  sealed class TIME(private vararg val stems: String) : Idea(*stems) {

    object YEAR : TIME("y", "г", "л")

    object MONTH : TIME("mon", "мес")

    object WEEK : TIME("week", "недел")

    object DAY : TIME("d", "д")

    object HOUR : TIME("h", "ч")

    object MINUTE : TIME("m", "м")

    object SECOND : TIME("s", "с")
  }
}