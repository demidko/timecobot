package features

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import toHumanView

internal class BalanceKtTest {

  @Test
  fun toHumanTime() {
    assertThat(0L.toHumanView(), equalTo("0s"))
    assertThat((60L * 60L * 24L * 365L).toHumanView(), equalTo("1yr"))
    assertThat((60L * 60L * 24L * 365 + 12).toHumanView(), equalTo("1yr 12s"))
    assertThat(1232443224L.toHumanView(), equalTo("39yr 29d 9h 20m 24s"))
  }
}