import semantic.*
import kotlin.time.*

sealed class Command

object Status : Command()

class Unexpected(val token: Token) : Command()

sealed class Mutable(val duration: Duration) : Command()

class Ban(duration: Duration) : Mutable(duration)

class Transfer(duration: Duration) : Mutable(duration)

class Redeem(duration: Duration) : Mutable(duration)

/**
 * Функция распознает команду из произвольного текста
 * на основе наборов нормализованных семантических представлений.
 */
fun String.recognize(): Command {
  val it = tokenize().iterator()
  val token = it.next()
  return when (token.semnorm) {
    is semantic.Status -> Status
    is semantic.Ban -> it.parse(::Ban)
    is semantic.Transfer -> it.parse(::Transfer)
    is semantic.Redeem -> it.parse(::Redeem)
    else -> Unexpected(token)
  }
}

private fun <T> Iterator<Token>.parse(ctor: (Duration) -> T) = ctor(parseDuration())

private fun Iterator<Token>.parseDuration(): Duration {
  val (token, norm) = next()
  return when (norm) {
    is Time -> norm.toDuration(parseInt())
    is Number -> parseTime().toDuration(token.toInt())
    else -> parseDuration()
  }
}

private fun Iterator<Token>.parseTime(): Time {
  val (_, norm) = next()
  return when (norm) {
    is Time -> norm
    else -> parseTime()
  }
}

private fun Iterator<Token>.parseInt(): Int {
  val (token, norm) = next()
  return when (norm) {
    is Number -> token.toInt()
    else -> parseInt()
  }
}

private fun Time.toDuration(number: Int) = when (this) {
  is Second -> number.seconds
  is Minute -> number.minutes
  is Hour -> number.hours
  is Day -> number.days
  is Week -> number.days * 7
  is Month -> number.days * 30
  is Year -> number.days * 365
}
