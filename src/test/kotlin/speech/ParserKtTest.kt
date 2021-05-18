package speech

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import commands.Ban
import commands.Help
import commands.Transfer
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class ParserKtTest {

  @Test
  fun toCommand() {
    assertThat("забань васю на 5 минут".parseCommand(), equalTo(Ban(minutes(5))))
    assertThat("забань на 40 с".parseCommand(), equalTo(Ban(seconds(40))))
    assertThat("mute 10s".parseCommand(), equalTo(Ban(seconds(10))))
  }

  @Test
  fun toCommandOtherCase() {
    val speech = "переведи часов наверное 17 Пете".parseCommand()
    assertThat(speech, equalTo(Transfer(hours(17))))
  }

  @Test
  fun helpTest() {
    assertThat("help".parseCommand(), equalTo(Help))
    assertThat("!help".parseCommand(), equalTo(commands.Balance))
  }

  @Test
  fun transferTest() {
    assertThat("send5d".parseCommand(), equalTo(Transfer(days(5))))
    assertThat("send 10h".parseCommand(), equalTo(Transfer(hours(10))))
  }


  @Test
  fun balanceTest() {
    assertThat("/!".parseCommand(), equalTo(commands.Balance))
    assertThat("счет".parseCommand(), equalTo(commands.Balance))
  }
}