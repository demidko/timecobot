package speech

import kotlin.time.*

/** Класс представляет собой полностью формализованную команду Telegram-боту */
sealed class Command

/** Команда проверки своего статуса */
object StatusCommand : Command()

/**
 * Команда забанить другого пользователя
 * @param duration время бана
 */
data class BanCommand(val duration: Duration) : Command()

/**
 * Команда перевода времени другому пользователю
 * @param duration время для перевода
 */
data class TransferCommand(val duration: Duration) : Command()

/** Команда выкупа другого пользователя из бана */
object FreeCommand : Command()

/** Команда запроса о помощи */
object HelpCommand : Command()

/** Команда обращения к разработчику */
object DebugCommand : Command()

/**
 * Функция распознает команду из произвольного текста
 * на основе наборов нормализованных семантических представлений.
 */
fun String.command() = tokens().iterator().parseCommand()

private fun Iterator<Token>.parseCommand(): Command? = when (next().semnorm) {
  is Status -> StatusCommand
  is Redeem -> FreeCommand
  is Ban -> parseDuration(::BanCommand)
  is Transfer -> parseDuration(::TransferCommand)
  is Help -> HelpCommand
  is Debug -> DebugCommand
  is Skip -> parseCommand()
  else -> null
}

private fun <T> Iterator<Token>.parseDuration(ctor: (Duration) -> T) = ctor(parseDuration())

private fun Iterator<Token>.parseDuration(): Duration {
  val (token, norm) = next()
  return when (norm) {
    is Time -> norm.toDuration(parseLong())
    is Number -> parseTime().toDuration(token.toLong())
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

private fun Iterator<Token>.parseLong(): Long {
  val (token, norm) = next()
  return when (norm) {
    is Number -> token.toLong()
    else -> parseLong()
  }
}

private fun Time.toDuration(number: Long) = when (this) {
  is Second -> number.seconds
  is Minute -> number.minutes
  is Hour -> number.hours
  is Day -> number.days
  is Week -> number.days * 7
  is Month -> number.days * 30
  is Year -> number.days * 365
}
