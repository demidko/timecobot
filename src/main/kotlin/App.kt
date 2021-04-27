import org.slf4j.LoggerFactory.getLogger
import storages.TimeBank
import telegram.timecoinsBot

fun main(args: Array<String>) {


  println(System.getenv("REDIS_URL"))
  println(System.getenv("REDIS_USER"))
  println(System.getenv("REDIS_USERNAME"))
  println(System.getenv("REDIS_PASSWORD"))

  println(System.getenv("DATABASE_URL"))
  println(System.getenv("DATABASE_USER"))
  println(System.getenv("DATABASE_USERNAME"))
  println(System.getenv("DATABASE_PASSWORD"))

  val bank = TimeBank()
  val log = getLogger("Bot")
  val bot = timecoinsBot(bank, log)
  bot.startPolling()
}



