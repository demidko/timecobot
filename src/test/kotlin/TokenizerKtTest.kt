import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import semnorms.*
import semnorms.Number
import semnorms.commands.Balance
import semnorms.commands.Command
import semnorms.commands.Help
import semnorms.commands.durable.Ban
import semnorms.commands.durable.Transfer

class TokenizerKtTest {

  @Test
  fun banTest() {
    assertThat(
      "mute 10s".tokenize(),
      equalTo(
        listOf(
          Token("mute", Ban), Token("10", Number), Token("s", Second)
        )
      )
    )
    assertThat(
      "забань васю на 5 минут".tokenize(), equalTo(
        listOf(
          Token("забань", Ban), Token("васю", null),
          Token("на", null), Token("5", Number),
          Token("минут", Minute)
        )
      )
    )
    assertThat(
      "забань на 40 с".tokenize(), equalTo(
        listOf(
          Token("забань", Ban), Token("на", null),
          Token("40", Number), Token("с", Second)
        )
      )
    )
  }

  @Test
  fun helpTest() {
    assertThat("help".tokenize(), equalTo(listOf(Token("help", Help))))
    assertThat("/help".tokenize(), equalTo(listOf(Token("/", Command), Token("help", Help))))
  }

  @Test
  fun transferTest() {
    assertThat(
      "переведи часов наверное 17 Пете".tokenize(),
      equalTo(
        listOf(
          Token("переведи", Transfer), Token("часов", Hour),
          Token("наверное", null), Token("17", Number),
          Token("Пете", null)
        )
      )
    )
    assertThat(
      "send5d".tokenize(),
      equalTo(
        listOf(
          Token("send", Transfer), Token("5", Number), Token("d", Day),
        )
      )
    )
    assertThat(
      "send 10h".tokenize(), equalTo(
        listOf(
          Token("send", Transfer), Token("10", Number), Token("h", Hour),
        )
      )
    )
  }


  @Test
  fun balanceTest() {
    assertThat(
      "/баланс".tokenize(), equalTo(
        listOf(
          Token("/", Command), Token("баланс", Balance)
        )
      )
    )
    assertThat(
      "счет".tokenize(), equalTo(
        listOf(
          Token("счет", Balance)
        )
      )
    )
  }
}