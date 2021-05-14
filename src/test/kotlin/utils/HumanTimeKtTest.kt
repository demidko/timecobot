package utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import speech.BanCommand
import speech.command
import toHumanTime
import kotlin.time.Duration.Companion.days

internal class HumanTimeKtTest {

  @Test
  fun toHumanTime() {
    assertThat(days(366).toHumanTime(), equalTo("1yr"));
    assertThat("ban1yr".command(), equalTo(BanCommand(days(366))));
  }
}