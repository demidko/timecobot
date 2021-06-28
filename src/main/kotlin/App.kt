import com.github.demidko.redis.utils.clientOf
import com.github.demidko.redis.utils.threadSafeMap
import org.redisson.Redisson.create
import java.lang.System.getenv

fun main() = create(clientOf(getenv("DATABASE_URL"))).run {
  Bot(
    getenv("TOKEN"),
    Storage(
      threadSafeMap("timecoins"),
      threadSafeMap("pinned_messages"),
      threadSafeMap("restricted_admins")
    )
  ).startPolling()
}
