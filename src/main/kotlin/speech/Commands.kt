package speech

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** Fully formalized command for the Telegram bot */
sealed class Command

/** Team to check your status */
object StatusCommand : Command()

/**
 * The command to ban another user
 * @param duration ban duration
 */
data class BanCommand(val duration: Duration) : Command()

/**
 * Command to transfer time to another user
 * @param duration time duration for transfer
 */
data class TransferCommand(val duration: Duration) : Command()

/** The command to buy another user out of the ban */
object FreeCommand : Command()

/** Help request command */
object HelpCommand : Command()

/**
 * The function recognizes a command from free text
 * based on sets of normalized semantic representations.
 */
fun String.command() = tokens().iterator().command()

private fun Iterator<Token>.command(): Command? = when (next().semnorm) {
  is Status -> StatusCommand
  is Redeem -> FreeCommand
  is Ban -> parseDuration(::BanCommand)
  is Transfer -> parseDuration(::TransferCommand)
  is Help -> HelpCommand
  is CommandSymbol -> command()
  else -> null
}

private fun <T> Iterator<Token>.parseDuration(ctor: (Duration) -> T) = ctor(
  try {
    parseDuration()
  } catch (e: RuntimeException) {
    error("Provide an time unit with integer for this command. For example ”1 day” or ”day 1”")
  }
)

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
  is Second -> seconds(number)
  is Minute -> minutes(number)
  is Hour -> hours(number)
  is Day -> days(number)
  is Week -> days(number) * 7
  is Month -> days(number) * 30
  is Year -> days(number) * 365
}
