package ml.demidko.timecobot

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import org.junit.jupiter.api.Test

class CommandTest {

  @Test
  fun isBan() {
    Command("Забанить вакцинаторов на 3 недели").run {
      assertThat(isBan).isTrue()
      assertThat(seconds).isEqualTo(60L * 60 * 24 * 7 * 3)
    }
    Command("бан5м").run {
      assertThat(isBan).isTrue()
      assertThat(seconds).isEqualTo(60L * 5)
    }
  }

  @Test
  fun isPin() {
    Command("закрепить на 30 сек").run {
      assertThat(isPin).isTrue()
      assertThat(seconds).isEqualTo(30L)
    }
  }
}
