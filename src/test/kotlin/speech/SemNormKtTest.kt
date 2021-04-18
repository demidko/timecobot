package speech

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class SemNormKtTest {

  @Test
  fun withSemNorms() {
    val command = "забань васю на 5 минут".tokenize()
    val norms = command.withSemNorms().map { it.second }
    assertThat(norms, equalTo(listOf(Ban, null, null, Number, Minute)))
  }
}