package semantic

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class TokenKtTest {

  @Test
  fun withSemNorms() {
    val command = "забань васю на 5 минут".tokenize().map(Token::semnorm)
    assertThat(command, equalTo(listOf(Ban, null, null, Number, Minute)))
  }
}