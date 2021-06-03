package semnorms

import PinnedMessages
import Timecoins
import Token
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import semnorms.Rule
import semnorms.Semnorm

abstract class Executable(vararg rules: Rule) : Semnorm(*rules) {

  abstract fun execute(
    token: Iterator<Token>,
    bot: Bot,
    message: Message,
    coins: Timecoins,
    pins: PinnedMessages
  )
}