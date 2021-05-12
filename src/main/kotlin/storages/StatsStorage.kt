package storages

import org.slf4j.LoggerFactory.getLogger
import java.util.concurrent.atomic.AtomicLong
import kotlin.concurrent.timer
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object StatsStorage {

  private val log = getLogger("Stat")
  private val perHourRequests = AtomicLong()
  private val perHourTime = AtomicLong()

  init {
    timer(period = hours(1).inWholeMilliseconds) {
      perHourRequests.set(0)
      perHourTime.set(0)
    }
    timer(period = minutes(5).inWholeMilliseconds) {
      val requests = perHourRequests.get()
      val time = perHourTime.get()
      log.info("$requests req/hour (~${time / requests} ms/req)")
    }
  }

  fun addRequestStat(elapsedTimeMs: Long) {
    perHourRequests.incrementAndGet()
    perHourTime.addAndGet(elapsedTimeMs)
  }
}