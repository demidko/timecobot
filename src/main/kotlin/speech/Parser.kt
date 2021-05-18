package speech

import commands.Ban
import commands.Command
import commands.Transfer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


/**
 * The function recognizes a command from free text
 * based on sets of normalized semantic representations.
 */
fun String.parseCommand() = try {
  tokenize().iterator().parseCommand()
} catch (e: RuntimeException) {
  null
}

private fun Iterator<Token>.parseCommand(): Command? = when (next().semnorm) {
  is speech.Balance -> commands.Balance
  is speech.Unban -> commands.Unban
  is speech.Ban -> parseDuration(::Ban)
  is speech.Transfer -> parseDuration(::Transfer)
  is speech.Help -> commands.Help
  is speech.CommandSymbol -> parseCommand()
  is speech.Pin -> commands.Pin
  else -> null
}

private fun <T> Iterator<Token>.parseDuration(ctor: (Duration) -> T) = try {
  ctor(parseDuration())
} catch (e: RuntimeException) {
  error("Provide an time unit with integer for this command. For example ”1 day” or ”day 1”")
}


private fun Iterator<Token>.parseDuration(): Duration {
  val (token, norm) = next()
  return when (norm) {
    is Time -> try {
      norm.toDuration(parseLong())
    } catch (ignored: RuntimeException) {
      norm.toDuration(1)
    }
    is Number -> try {
      parseTime().toDuration(token.toLong())
    } catch (ignored: RuntimeException) {
      Minute.toDuration(token.toLong())
    }
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
