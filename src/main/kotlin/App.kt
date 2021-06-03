import org.redisson.Redisson.create
import org.slf4j.LoggerFactory
import java.lang.System.getenv


fun main() {

  val redis = create(clientOf(getenv("DATABASE_URL")))
  val coins: Timecoins = redis.mapOrLocal("timecoins")
  val pins: PinnedMessages = redis.mapOrLocal("pins")
  val bot = Bot(getenv("TOKEN"), coins, pins)

  //coins.schedulePayments(minutes(24))
  //bot.scheduleUnpinMessages(pins)

  LoggerFactory.getLogger("BB").info("start")
  bot.startPolling()
}