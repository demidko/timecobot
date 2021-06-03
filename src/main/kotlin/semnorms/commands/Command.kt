package semnorms.commands

import PinnedMessages
import Timecoins
import Token
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import execute
import semnorms.Executable
import semnorms.stem

/** Skip telegram-command prefix '/' */
object Command : Executable(stem("/")) {
  override fun execute(
    token: Iterator<Token>,
    bot: Bot,
    message: Message,
    coins: Timecoins,
    pins: PinnedMessages
  ) = bot.execute(token, message, coins, pins)
}