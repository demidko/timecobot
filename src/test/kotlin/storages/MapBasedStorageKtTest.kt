package storages

import org.junit.jupiter.api.Test
import org.redisson.Redisson.create
import org.redisson.config.Config
import java.lang.System.getenv
import kotlin.streams.toList

internal class MapBasedStorageKtTest {



  fun mapBasedStorage() {
    val config = Config().apply {
      useSingleServer().apply {
        username = ""
        password = ""
        address = ""
      }
    }
    val client = create(config)
    val map = client.getMap<Long, Long>("test-redis2")
    map[1] = 4
    println(client.keys.keysStream.toList())
  }
}