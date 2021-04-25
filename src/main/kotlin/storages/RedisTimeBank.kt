package storages

import storages.InMemoryTimeBank.seeMap
import kotlin.concurrent.timer
import kotlin.time.seconds

class RedisTimeBank : TimeBank by InMemoryTimeBank {

  init {
    timer(period = 30.seconds.toLongMilliseconds()) {
      seeMap {
        TODO("save in-memory key-value db to redis cluster")
      }
    }
  }
}