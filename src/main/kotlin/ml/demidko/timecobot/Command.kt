package ml.demidko.timecobot

class Command(text: String) {

  private companion object {
    const val MINUTE_SECONDS = 60L
    const val HOUR_SECONDS = MINUTE_SECONDS * 60
    const val DAY_SECONDS = HOUR_SECONDS * 24
    const val WEEK_SECONDS = DAY_SECONDS * 7
    const val MONTH_SECONDS = DAY_SECONDS * 30
    const val YEAR_SECONDS = DAY_SECONDS * 365
    const val MIN_SECONDS = 30L
    const val MAX_SECONDS = DAY_SECONDS * 366
  }

  private val text = text.trim().lowercase()

  val isBan
    get() = startStems(
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
      "мьют",
      "замьют",
      "💥"
    )

  val isUnban
    get() = startStems(
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

  val isTransfer
    get() =
      startWords("дать") ||
        startStems(
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
          "отправ",
          "send"
        )

  val isFaq
    get() = startStems(
      "помощ",
      "справк",
      "правил",
      "help",
      "rule",
      "faq",
      "onUpdateAction",
      "старт"
    )

  val isBalance
    get() = startStems(
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

  val isPin
    get() = startWords("пин") || startStems("закреп", "pin", "запин")

  val isUnpin
    get() = startWords("пин") || startStems("откреп", "unpin", "отпин")

  val seconds: Long
    get() {
      val s = timeUnit * number
      return when {
        s < MIN_SECONDS -> MIN_SECONDS
        s > MAX_SECONDS -> MAX_SECONDS
        else -> s
      }
    }

  private val number: Long
    get() {
      val first = text.indexOfFirst(Char::isDigit)
      if (first == -1) {
        return 1
      }
      for (last in (first + 1) until text.length) {
        if (!text[last].isDigit()) {
          return text.substring(first until last).toLong()
        }
      }
      return text.substring(first).toLong()
    }

  /**
   * Единица времени в секундах
   */
  private val timeUnit: Long
    get() {
      return when {
        stems("sec", "сек") || words("s", "с") -> 1
        stems("час", "hour") || words("h", "ч") -> HOUR_SECONDS
        stems("day", "суток", "сутк", "дня", "ден", "дне")
          || words("d", "д") -> DAY_SECONDS
        stems("week", "недел") -> WEEK_SECONDS
        stems("mon", "mo", "мес") -> MONTH_SECONDS
        stems("year", "год", "лет") || words("y", "yr", "г", "л") -> YEAR_SECONDS
        else -> MINUTE_SECONDS
      }
    }

  /**
   * @param stem all stems must be in lower case.
   */
  private fun startStems(vararg stem: String) = stem.any(text::startsWith)

  /**
   * @param word all words must be in lower case.
   */
  private fun startWords(vararg word: String) =
    word.any { text == it || (text.startsWith(it) && !text[it.length].isLetter()) }

  /**
   * @param stem all words must be in lower case.
   */
  private fun stems(vararg stem: String) = stem.any(text::contains)

  /**
   * @param word all words must be in lower case.
   */
  private fun words(vararg word: String) = word.any {
    val first = text.indexOf(it)
    if (first == -1 || (first > 0 && text[first].isLetter())) {
      return@any false
    }
    val last = first + it.length
    if (last < text.length && text[last].isLetter()) {
      return@any false
    }
    return@any true
  }
}