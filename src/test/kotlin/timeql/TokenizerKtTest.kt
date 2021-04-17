package timeql

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

internal class TokenizerKtTest {

  @Test
  fun tokenize() {
    val actual =
      "view2bull-action1 { keyName=\"click-respond\", context=\"vacancy-summary-button\" }".tokenize()
    val expected = listOf(
      "view", "2", "bull-action", "1",
      "{",
      "keyName",
      "=",
      "\"click-respond\"",
      ",",
      "context",
      "=",
      "\"vacancy-summary-button\"",
      "}"
    )
    assertThat(actual, equalTo(expected))
  }

  @Test
  fun degenerateCases() {
    assertThat("..".tokenize(), equalTo(listOf(".", ".")))
    assertThat("{}".tokenize(), equalTo(listOf("{", "}")))
  }

  @Test
  fun tokenizeNames() {
    assertThat("2names".tokenize(), equalTo(listOf("2", "names")))
    assertThat("names12".tokenize(), equalTo(listOf("names", "12")))
  }

  @Test
  fun escaping() {
    val actual = """
      "abc\"dfg"
    """.trimIndent()
    assertThat(actual.tokenize(), equalTo(listOf("\"abc\\\"dfg\"")))
  }
}