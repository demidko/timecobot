package speech

/**  Токен состоит из лексемы и ее семантической нормы */
data class Token(val lexeme: String, val semnorm: Semnorm?)

/** Эта функция является ядром токенайзера, обеспечивающим разбор токенов за линейное время. */
fun String.tokens(): List<Token> = when (val diff = indexOfFirstDiff()) {
  -1 -> grep()
  else -> substring(0 until diff).grep() + substring(diff until length).tokens()
}

/** Обработка обнаруженных лексем */
private fun String.grep() = when (isBlank()) {
  true -> emptyList()
  else -> listOf(Token(this, semnorm))
}

/** @return первый отличный по своему типу от предыдущих символ либо -1 */
private fun String.indexOfFirstDiff(): Int {
  if (length > 1) {
    for (idx in (1 until length)) {
      if (isDiff(0, idx)) {
        return idx
      }
    }
  }
  return -1
}

/**
 * Переопределяем понятие разницы между двумя символами, на основе которой строка бьется на лексемы.
 * @param typeIdx первый индекс символа задает тип токена
 * @param otherIdx и сравнивается с символом второго индекса
 * @return является ли второй индекс разбивающим символом?
 */
private fun String.isDiff(typeIdx: Int, otherIdx: Int): Boolean {

  // убеждаемся что символы в наличии
  val type = get(typeIdx)
  val other = get(otherIdx)

  // обрабатываем имена программных сущностей
  if (type.isLetter()) {
    // Разрешаем имена через дефис и нижнее подчеркивание
    // В остальных случаях считаем что ввод имени завершился
    return (other !in "-_") && !other.isLetter()
  }

  // обрабатываем символы которые являются разбивающими в любом случае
  if (type in ".,{}=:<>/\\") {
    return true
  }

  // обрабататываем строки вида '...' и "..." с экранированием управляющих символов
  if (type == '"') {
    return getOrNull(otherIdx - 1) == '"'
      && getOrNull(otherIdx - 2) != '\\'
      && otherIdx - 1 != typeIdx
  }
  if (type == '\'') {
    return getOrNull(otherIdx - 1) == '\''
      && getOrNull(otherIdx - 2) != '\\'
      && otherIdx - 1 != typeIdx
  }

  // обрабататываем строки вида `...` без экранирования управляющих символов
  if (type == '`') {
    return getOrNull(otherIdx - 1) == '`'
      && otherIdx - 1 != typeIdx
  }

  // если понятие разницы не было переопределено в условиях выше,
  // используем стандартную проверку на тип символа
  return type.category != other.category
}