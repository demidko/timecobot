package utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import commands.Ban
import org.junit.jupiter.api.Test
import print
import printSeconds
import speech.parseCommand
import kotlin.time.Duration.Companion.days

class TimePrinterKtTest {

  @Test
  fun toHumanTime() {
    assertThat(days(365).print(), equalTo("1yr"))
    assertThat("ban1yr".parseCommand(), equalTo(Ban(days(365))))
    assertThat(days(365).inWholeSeconds.printSeconds(), equalTo("1yr"))
  }
}