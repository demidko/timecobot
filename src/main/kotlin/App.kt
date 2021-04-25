import org.slf4j.LoggerFactory.getLogger
import storages.TimeBank
import telegram.timecoinsBot

fun main(args: Array<String>) {
  val bank = TimeBank()
  val log = getLogger("Bot")
  val bot = timecoinsBot(bank, log)
  bot.startPolling()
}



