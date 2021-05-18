package commands

import com.github.kotlintelegrambot.Bot
import com.github.kotlintelegrambot.entities.Message

/** Fully formalized command for the Telegram bot */
interface Command {

  /**
   * Execute command
   * @param bot bot
   * @param m command message
   */
  fun execute(bot: Bot, m: Message)
}