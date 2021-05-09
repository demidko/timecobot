package storages

import org.redisson.Redisson
import org.redisson.config.Config
import java.lang.System.getenv

/**
 * Configure redis client
 * @param address connection string with login and password
 * @return config object
 */
fun redisClient(address: String) = Config().apply {
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

/** To use a Redis cluster you need to set the DATABASE_URL environment variable */
fun <K, V> redisMap(name: String) =
  getenv("DATABASE_URL")
    .let(::redisClient)
    .let(Redisson::create)
    .getMap<K, V>(name)

