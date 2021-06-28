import com.github.demidko.redis.utils.clientOf
import com.github.demidko.redis.utils.redisMap
import com.github.demidko.redis.utils.threadSafeMap
import ml.demidko.timecobot.Storage
import org.redisson.Redisson.create
import java.lang.System.getenv

fun main() = create(clientOf(getenv("DATABASE_URL"))).run {
  Bot(
    getenv("TOKEN"),
    Storage(
      redisMap("timecoins"),
      redisMap("pinned_messages"),
      redisMap("restricted_admins")
    )
  ).startPolling()
}
