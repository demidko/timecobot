package speech

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class TokensKtTest {

  @Test
  fun semnormsTest() {
    val command = "забань васю на 5 минут".tokenize().map(Token::semnorm)
    assertThat(command, equalTo(listOf(Ban, null, null, Number, Minute)))
  }

  @Test
  fun statusTest() {
    assertThat("счет".tokenize(), equalTo(listOf(Token("счет", Status))))
  }

  @Test
  fun helpTest() {
    assertThat("help".tokenize(), equalTo(listOf(Token("help", Help))))
    assertThat("/help".tokenize(), equalTo(listOf(Token("/", CommandSymbol), Token("help", Help))))
    assertThat(
      "///help".tokenize(),
      equalTo(listOf(Token("///", CommandSymbol), Token("help", Help)))
    )
  }

  @Test
  fun muteTest() {
    assertThat(
      "mute 10s".tokenize(),
      equalTo(listOf(Token("mute", Ban), Token("10", Number), Token("s", Second)))
    )
  }
}