import com.github.kotlintelegrambot.dispatcher.handlers.TextHandlerEnvironment
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class RouterKtTest {

  @Test
  fun routeRequest() {
    TextHandlerEnvironment(mockk(), mockk(), mockk(), "craaaack").routeRequest()
    assertThat(perHourStat.get(), equalTo(0))
  }
}