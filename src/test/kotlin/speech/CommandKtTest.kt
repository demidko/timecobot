package speech

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.days
import kotlin.time.hours
import kotlin.time.minutes
import kotlin.time.seconds

class CommandKtTest {

  @Test
  fun toCommand() {
    val speech = "забань васю на 5 минут".command()
    assertThat(speech, equalTo(BanCommand(5.minutes)))
  }

  @Test
  fun toCommandOtherCase() {
    val speech = "переведи часов наверное 17 Пете".command()
    assertThat(speech, equalTo(TransferCommand(17.hours)))
  }

  @Test
  fun checkStatus() {
    assertThat("счет".command(), equalTo(StatusCommand))
  }

  @Test
  fun brokenCase() {
    assertThat("забань на 40 с".command(), equalTo(BanCommand(40.seconds)))
  }

  @Test
  fun helpTest() {
    assertThat("help".command(), equalTo(HelpCommand))
  }

  @Test
  fun transferTest() {
    assertThat("+5d".command(), equalTo(TransferCommand(5.days)))
  }
}