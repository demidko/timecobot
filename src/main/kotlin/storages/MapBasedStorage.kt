package storages

import kotlin.time.Duration

fun mapBasedStorage(): MutableMap<Long, Duration> {
  return LinkedHashMap()
}