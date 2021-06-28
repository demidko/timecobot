package ml.demidko.timecobot

import com.github.demidko.tokenizer.Token
import com.github.demidko.tokenizer.tokenize
import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message
import ml.demidko.timecobot.semnorms.*
import ml.demidko.timecobot.semnorms.Number
import ml.demidko.timecobot.semnorms.commands.Balance
import ml.demidko.timecobot.semnorms.commands.Help
import ml.demidko.timecobot.semnorms.commands.Skip
import ml.demidko.timecobot.semnorms.commands.Unban
import ml.demidko.timecobot.semnorms.commands.durable.Ban
import ml.demidko.timecobot.semnorms.commands.durable.Pin
import ml.demidko.timecobot.semnorms.commands.durable.Transfer

// TODO: класс подлежит переносу в библиотеку нормализованных семантических представлений

class Query(
  val bot: Bot,
  val storage: Storage,
  val message: Message,
  val token: Iterator<Token<Semnorm?>>
) {

  constructor(bot: Bot, storage: Storage, message: Message) : this(
    bot,
    storage,
    message,
    message.text!!.semnorms().iterator()
  )

  fun execute() {
    if (token.hasNext()) {
      when (val semnorm = token.next().type) {
        is Executable ->
          semnorm.execute(this)
      }
    }
  }

  private companion object {

    /** list of all useful ml.demidko.timecobot.semnorms */
    private val semnorms = listOf(
      Skip, Number,
      Year, Month, Week, Day, Hour, Minute, Second,
      Balance, Transfer, Ban, Unban, Help, Pin
    )

    fun String.semnorms() = tokenize { semnorms.firstOrNull(lowercase()::matches) }
  }
}