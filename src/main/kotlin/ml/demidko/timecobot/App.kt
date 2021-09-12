package ml.demidko.timecobot

import Gun
import Promoter
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.logging.LogLevel.Error
import org.slf4j.LoggerFactory.getLogger
import java.lang.System.getenv
import java.util.concurrent.ExecutionException

fun main() {
  val log = getLogger("Bot")
  val owner = getenv("OWNER")?.toLong()?.telegramId()
  lateinit var bank: Bank
  lateinit var promoter: Promoter
  lateinit var gun: Gun
  val bot = bot {
    token = getenv("TOKEN")
    logLevel = Error
    dispatch {
      command("balance") {
        if (!gun.muted(message)) {
          bank.inform(message)
        }
      }
      command("help") {
        if (!gun.muted(message)) {
          bot.faq(message)
        }
      }
      message {
        if (!gun.muted(message)) {
          val command = Command(message.text ?: return@message)
          try {
            when {
              command.isBan -> gun.mute(message, command.seconds)
              command.isUnban -> gun.unmute(message)
              command.isPin -> promoter.pin(message, command.seconds)
              command.isUnpin -> promoter.unpin(message)
              command.isTransfer -> bank.transfer(command.seconds, message)
              command.isFaq -> bot.faq(message)
              command.isBalance -> bank.inform(message)
            }
          } catch (e: RuntimeException) {
            owner?.let { bot.sendMessage(it, e.stackTraceToString()) }
            log.error(e.message, e)
          } catch (e: ExecutionException) {
            owner?.let { bot.sendMessage(it, e.stackTraceToString()) }
            log.error(e.cause?.message, e.cause)
          }
        }
      }
    }
  }
  val redis = redisOf(getenv("REDIS"))
  bank = Bank(bot, redis.getMap("bank"))
  promoter = Promoter(bot, bank, redis.getMap("promoter"))
  gun = Gun(bot, bank, redis.getMap("gun"))
  bot.startPolling()
  owner?.let { bot.sendMessage(it, "Successful deployment") }
}