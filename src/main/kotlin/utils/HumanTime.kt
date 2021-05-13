private const val years = 60L * 60L * 24L * 30L * 12L

/** seconds to human time duration */
fun Long.toHumanTime() = when (val time = grepYears()) {
  "" -> "You don't have time"
  else -> "You have $time"
}

fun Long.grepYears() = when (val count = div(years)) {
  0L -> grepMonth()
  1L -> "$count year${minus(count * years).grepMonth()}"
  else -> "$count years${minus(count * years).grepMonth()}"
}

private const val months = 60L * 60L * 24L * 30L

private fun Long.grepMonth() = when (val count = div(months)) {
  0L -> grepDays()
  1L -> " $count month${minus(count * months).grepDays()}"
  else -> " $count months${minus(count * months).grepDays()}"
}

private const val days = 60L * 60L * 24L

private fun Long.grepDays() = when (val count = div(days)) {
  0L -> grepHours()
  else -> " ${count}d${minus(count * days).grepHours()}"
}

private const val hours = 60L * 60L

private fun Long.grepHours() = when (val count = div(hours)) {
  0L -> grepMinutes()
  else -> " ${count}h${minus(count * hours).grepMinutes()}"
}

private const val minutes = 60L

private fun Long.grepMinutes() = when (val count = div(minutes)) {
  0L -> grepSeconds()
  else -> " ${count}m${minus(count * minutes).grepSeconds()}"
}

private fun Long.grepSeconds() = when (this) {
  0L -> ""
  else -> " ${this}s"
}