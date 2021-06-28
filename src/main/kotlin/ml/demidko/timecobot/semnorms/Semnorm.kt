package ml.demidko.timecobot.semnorms

typealias Rule = (String) -> Boolean

/** Normalized semantic representation: defined by the rules that apply to lexemes. */
abstract class Semnorm(vararg val rules: Rule)

/** Weak match rule (word's stem) */
fun stem(vararg stems: String): Rule = { stems.any(it::startsWith) }

/** Strict match rule (whole word) */
fun word(vararg words: String): Rule = { words.any(it::equals) }

/**
 * Check if the word matches the semantic norm?
 * Attention! Case sensitive
 */
fun String.matches(norm: Semnorm) = norm.rules.any { it(this) }