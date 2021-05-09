package storages

import org.redisson.Redisson
import org.redisson.config.Config
import java.lang.System.getenv

/**
 * Configure redis client
 * @param address connection string with login and password
 * @return config object
 */
fun redisConfig(address: String) = Config().apply {
  useSingleServer().apply {

    this.address = address

    val authorizationIdx = 9

    username = address
      .substring(authorizationIdx)
      .substringBefore(':')

    password = address
      .substring(authorizationIdx + username.length + 1)
      .substringBefore('@')
  }
}

private val redissonClient by lazy {
  getenv("DATABASE_URL")
    .let(::redisConfig)
    .let(Redisson::create)
}

fun <K, V> redisMap(name: String) = redissonClient.getMap<K, V>(name)

fun <T> redisSet(name: String) = redissonClient.getSet<T>(name)



