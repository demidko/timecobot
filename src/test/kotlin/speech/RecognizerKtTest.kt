package speech

import Command.MutableCommand
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import kotlin.time.hours
import kotlin.time.minutes

class RecognizerKtTest {

  @Test
  fun toCommand() {
    val speech = "забань васю на 5 минут".recognize()
    val command = MutableCommand(Ban, 5.minutes)
    assertThat(speech, equalTo(command))
  }

  @Test
  fun toCommandOtherCase() {
    val speech = "переведи часов наверное 17 Пете".recognize()
    val command = MutableCommand(Transfer, 17.hours)
    assertThat(speech, equalTo(command))
  }
}