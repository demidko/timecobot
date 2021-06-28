import com.github.demidko.redis.utils.clientOf
import com.github.demidko.redis.utils.redisMap
import ml.demidko.timecobot.Storage
import org.redisson.Redisson.create
import java.lang.System.getenv

fun main() = create(clientOf(getenv("DATABASE_URL"))).run {
  Bot(
    getenv("TOKEN"),
    Storage(redisMap("timecoins"))
  ).startPolling()
}
