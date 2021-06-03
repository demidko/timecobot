package semnorms.commands

import PinnedMessages
import Timecoins
import Token
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import semnorms.Executable
import semnorms.Minute.toDuration
import semnorms.Rule
import semnorms.Time
import kotlin.time.Duration

abstract class Durable(vararg rules: Rule) : Executable(*rules) {

  final override fun execute(
    token: Iterator<Token>,
    bot: Bot,
    message: Message,
    coins: Timecoins,
    pins: PinnedMessages
  ) = execute(bot, message, token.parseDuration(), coins, pins)

  abstract fun execute(
    bot: Bot,
    message: Message,
    duration: Duration,
    coins: Timecoins,
    pins: PinnedMessages
  )
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
      toDuration(token.toLong())
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