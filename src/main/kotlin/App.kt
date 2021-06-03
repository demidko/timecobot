import org.redisson.Redisson.create
import java.lang.System.getenv
import kotlin.time.Duration.Companion.minutes


fun main() {

  val redis = create(clientOf(getenv("DATABASE_URL")))
  val coins: Timecoins = redis.mapOrLocal("timecoins")
  val pins: PinnedMessages = redis.mapOrLocal("pins")
  val bot = Bot(getenv("TOKEN"), coins, pins)

  coins.schedulePayments(minutes(24))
  bot.scheduleUnpinMessages(pins)
  bot.startPolling()
}