package storages

import org.redisson.Redisson
import org.redisson.config.Config
import org.slf4j.LoggerFactory
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
    LoggerFactory.getLogger("Redis").error(e.message, e)
    LinkedHashMap()
  }
}