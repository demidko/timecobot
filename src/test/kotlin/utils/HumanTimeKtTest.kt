package utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import speech.BanCommand
import speech.command
import toHumanView
import kotlin.time.Duration.Companion.days

internal class HumanTimeKtTest {

  @Test
  fun toHumanTime() {
    assertThat(days(365).toHumanView(), equalTo("1yr"))
    assertThat("ban1yr".command(), equalTo(BanCommand(days(365))))
    assertThat(days(365).inWholeSeconds.toHumanView(), equalTo("1yr"))
  }
}