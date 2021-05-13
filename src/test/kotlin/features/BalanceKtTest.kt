package features

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import toHumanTime

internal class BalanceKtTest {

  @Test
  fun toHumanTime() {
    assertThat(0L.toHumanTime(), equalTo("0s"))
    assertThat((60L * 60L * 24L * 30L * 12L).toHumanTime(), equalTo("1yr"))
    assertThat((60L * 60L * 24L * 30L * 12L + 12).toHumanTime(), equalTo("1yr 12s"))
    assertThat(1232443224L.toHumanTime(), equalTo("39yr 7mo 14d 9h 20m 24s"))
  }
}