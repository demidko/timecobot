package timeql

import timeql.Idea.Companion.recognizeIdeaOrNull


/**
 * Переводим набор токенов из контекстно свободной грамматики `забанить на 5 минут`
 * в промежуточное представление известных нам идей.
 */
fun List<String>.ideas() = map(::recognizeIdeaOrNull)
