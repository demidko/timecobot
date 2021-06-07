import org.redisson.Redisson.create
import java.lang.System.getenv
import kotlin.time.Duration.Companion.seconds


fun main() {

  val redis = create(clientOf(getenv("DATABASE_URL")))
  val coins: Timecoins = redis.getDatabase("timecoins")
  val pins: PinnedMessages = redis.getDatabase("pins")
  val bot = Bot(getenv("TOKEN"), coins, pins)
  coins.schedulePayments(seconds(60))
  //bot.scheduleUnpinMessages(pins)
  bot.startPolling()
}