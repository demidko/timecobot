package speech

/**  Токен состоит из лексемы и ее семантической нормы */
data class Token(val lexeme: String, val semnorm: Semnorm?)

/**
 * Функция осуществляет лексический и последующий семантический анализ:
 *   разбивает фразу и выделяет слова со смысловым оттенком.
 * @return набор токенов (лексема, смысл)
 */
fun String.tokens() = lexemes().map { Token(it, it.semnorm) }