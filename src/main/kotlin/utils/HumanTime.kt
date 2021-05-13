import kotlin.time.Duration

fun Duration.toHumanTime() = inWholeSeconds.toHumanTime()

private const val years = 60L * 60L * 24L * 30L * 12L

/** seconds to human time duration */
fun Long.toHumanTime() = when (val t = grepYears()) {
  "" -> "0s"
  else -> t.trim()
}

fun Long.grepYears() = when (val count = div(years)) {
  0L -> grepMonth()
  else -> "${count}yr ${minus(count * years).grepMonth()}"
}

private const val months = 60L * 60L * 24L * 30L

private fun Long.grepMonth() = when (val count = div(months)) {
  0L -> grepDays()
  else -> "${count}mo ${minus(count * months).grepDays()}"
}

private const val days = 60L * 60L * 24L

private fun Long.grepDays() = when (val count = div(days)) {
  0L -> grepHours()
  else -> "${count}d ${minus(count * days).grepHours()}"
}

private const val hours = 60L * 60L

private fun Long.grepHours() = when (val count = div(hours)) {
  0L -> grepMinutes()
  else -> "${count}h ${minus(count * hours).grepMinutes()}"
}

private const val minutes = 60L

private fun Long.grepMinutes() = when (val count = div(minutes)) {
  0L -> grepSeconds()
  else -> "${count}m ${minus(count * minutes).grepSeconds()}"
}

private fun Long.grepSeconds() = when (this) {
  0L -> ""
  else -> "${this}s"
}