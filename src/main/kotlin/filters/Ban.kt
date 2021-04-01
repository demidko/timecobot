package filters

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter

object Ban : Filter {
  override fun Message.predicate() = text?.startsWith('-') ?: false
}