package storages

import org.redisson.Redisson
import org.redisson.config.Config
import org.slf4j.LoggerFactory.getLogger
import kotlin.time.Duration

fun mapBasedStorage(): MutableMap<Long, Duration> {
  return try {
    Config()
      .apply {
        useClusterServers()
          .addNodeAddress(System.getenv("DATABASE_URL"))
      }
      .let(Redisson::create)
      .getMap("timecoins")
  } catch (e: RuntimeException) {
    getLogger("Redis").warn(e.message, e)
    LinkedHashMap()
  }
}