package speech

/**  Токен состоит из лексемы и ее семантической нормы */
data class Token(val lexeme: String, val semnorm: Semnorm?)

fun String.tokens() = lexemes().map { Token(it, it.semnorm) }