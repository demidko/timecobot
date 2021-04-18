package timeql

/**
 * Функция осуществляет токенизацию и начальный лексический анализ:
 * выделение имен, управляющих инструкций, строковых и числовых литералов
 */
fun String.lexemes() = splitByDifference().filterNot(String::isBlank)

/** Эта функция является ядром токенизатора, обеспечивающим разбор за линейное время. */
private fun String.splitByDifference(): List<String> =
  when (val diff = indexOfFirstDiff()) {
    -1 ->
      listOf(this)
    else ->
      listOf(substring(0 until diff)) +
        substring(diff until length).splitByDifference()
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
 * Переопределяем понятие разницы между двумя символами, на основе которой строка бьется на
 * токены.
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
  if (type in ".,{}=:<>") {
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
  return type.category != other.category
}