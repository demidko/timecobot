package speech

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CommandKtTest {

  @Test
  fun toCommand() {
    val speech = "забань васю на 5 минут".command()
    assertThat(speech, equalTo(BanCommand(minutes(5))))
  }

  @Test
  fun toCommandOtherCase() {
    val speech = "переведи часов наверное 17 Пете".command()
    assertThat(speech, equalTo(TransferCommand(hours(17))))
  }

  @Test
  fun checkStatus() {
    assertThat("счет".command(), equalTo(StatusCommand))
  }

  @Test
  fun brokenCase() {
    assertThat("забань на 40 с".command(), equalTo(BanCommand(seconds(40))))
  }

  @Test
  fun helpTest() {
    assertThat("help".command(), equalTo(HelpCommand))
  }

  @Test
  fun transferTest() {
    assertThat("+5d".command(), equalTo(TransferCommand(days(5))))
  }

  @Test
  fun muteTest() {
    assertThat("mute 10s".command(), equalTo(BanCommand(seconds(10))))
  }

  @Test
  fun sendTest() {
    assertThat("send 10h".command(), equalTo(TransferCommand(hours(10))))
  }

  @Test
  fun statusSymbolTest() {
    assertThat("/!".command(), equalTo(StatusCommand))
  }
}