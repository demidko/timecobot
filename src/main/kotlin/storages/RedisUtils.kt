package storages

import org.redisson.config.Config

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

