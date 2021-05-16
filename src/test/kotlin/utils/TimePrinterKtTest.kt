package utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import speech.BanCommand
import speech.command
import printSeconds
import print
import kotlin.time.Duration.Companion.days

class TimePrinterKtTest {

  @Test
  fun toHumanTime() {
    assertThat(days(365).print(), equalTo("1yr"))
    assertThat("ban1yr".command(), equalTo(BanCommand(days(365))))
    assertThat(days(365).inWholeSeconds.printSeconds(), equalTo("1yr"))
  }
}