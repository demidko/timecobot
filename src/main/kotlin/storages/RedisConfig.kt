package storages

import org.redisson.config.Config

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