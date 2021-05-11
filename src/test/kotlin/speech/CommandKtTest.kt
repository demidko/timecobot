package speech

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CommandKtTest {

  @Test
  fun toCommand() {
    val speech = "забань васю на 5 минут".parseCommand()
    assertThat(speech, equalTo(BanCommand(minutes(5))))
  }

  @Test
  fun toCommandOtherCase() {
    val speech = "переведи часов наверное 17 Пете".parseCommand()
    assertThat(speech, equalTo(TransferCommand(hours(17))))
  }

  @Test
  fun checkStatus() {
    assertThat("счет".parseCommand(), equalTo(StatusCommand))
  }

  @Test
  fun brokenCase() {
    assertThat("забань на 40 с".parseCommand(), equalTo(BanCommand(seconds(40))))
  }

  @Test
  fun helpTest() {
    assertThat("help".parseCommand(), equalTo(HelpCommand))
  }

  @Test
  fun transferTest() {
    assertThat("+5d".parseCommand(), equalTo(TransferCommand(days(5))))
  }

  @Test
  fun muteTest() {
    assertThat("mute 10s".parseCommand(), equalTo(BanCommand(seconds(10))))
  }

  @Test
  fun sendTest() {
    assertThat("send 10h".parseCommand(), equalTo(TransferCommand(hours(10))))
  }
}