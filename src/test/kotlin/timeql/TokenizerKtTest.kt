package timeql

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class TokenizerKtTest {

  @Test
  fun tokenize() {
    val actual = "view2bull-action-1 { keyName=\"click-respond\", context=\"vacancy-summary-button\" }".tokenize()
    val expected = listOf(
      "view2bull-action-1",
      "{",
      "keyName",
      "=",
      "\"click-respond\"",
      ",",
      "context",
      "=",
      "\"vacancy-summary-button\"",
      "}")
    assertThat(actual, equalTo(expected))
  }

  @Test
  fun degenerateCases() {
    assertThat("..".tokenize(), equalTo(listOf(".", ".")))
    assertThat("{}".tokenize(), equalTo(listOf("{", "}")))
  }

  @Test
  fun tokenizeNames() {
    val actual = "".tokenize()
    val expected = listOf(
      "view2bull-action-1",
      "{",
      "keyName",
      "=",
      "\"click-respond\"",
      ",",
      "context",
      "=",
      "\"vacancy-summary-button\"",
      "}")
    assertThat(actual, equalTo(expected))
  }
}