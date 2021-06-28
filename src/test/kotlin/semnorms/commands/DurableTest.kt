package semnorms.commands

import Query
import com.github.kotlintelegrambot.entities.Message
import io.mockk.mockk
import org.junit.jupiter.api.Test

internal class DurableTest {


  @Test
  fun testCommandDuration() {

    val q = Query(
      mockk(relaxed = true),
      mockk(relaxed = true),
      Message(
        text = "забанить на 7 часов",
        chat = mockk(relaxed = true),
        date = 1,
        messageId = 1
      )
    )

  }
}