package speech

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class TokensKtTest {

  @Test
  fun semnormsTest() {
    val command = "забань васю на 5 минут".tokens().map(Token::semnorm)
    assertThat(command, equalTo(listOf(Ban, null, null, Number, Minute)))
  }

  @Test
  fun statusTest() {
    assertThat("счет".tokens(), equalTo(listOf(Token("счет", Status))))
  }
}