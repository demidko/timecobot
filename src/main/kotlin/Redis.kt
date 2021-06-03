import co.touchlab.stately.isolate.IsolateState
import org.redisson.api.RedissonClient
import org.redisson.config.Config
import org.slf4j.LoggerFactory.getLogger

/**
 * Configure redis client
 * @param url connection string with login and password
 * @return config object
 */
fun clientOf(url: String) = Config().apply {
  useSingleServer().apply {
    address = url
    val authorizationIdx = 9
    username = url
      .substring(authorizationIdx)
      .substringBefore(':')
    password = url
      .substring(authorizationIdx + username.length + 1)
      .substringBefore('@')
  }
}

/**
 * Returns Redis map instance by name or local map.
 *
 * @param <K> type of key
 * @param <V> type of value
 * @param name - name of object
 * @return Map object
 */
fun <K, V> RedissonClient.mapOrLocal(name: String) = IsolateState {
  try {
    getMap<K, V>(name)
  } catch (e: RuntimeException) {
    getLogger(name).warn("${e.message}. In-memory database will be used")
    LinkedHashMap()
  }
}