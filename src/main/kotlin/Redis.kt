import co.touchlab.stately.isolate.IsolateState
import org.redisson.api.RedissonClient
import org.redisson.config.Config

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
 * Returns Redis safe isolated map instance by name or local map.
 *
 * @param <K> type of key
 * @param <V> type of value
 * @param name - name of object
 * @return isolated map object
 */
fun <K, V> RedissonClient.getDatabase(name: String): IsolateState<MutableMap<K, V>> =
  IsolateState {
    getMap(name)
  }