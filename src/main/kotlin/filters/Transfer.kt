package filters

import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.extensions.filters.Filter

object Transfer : Filter {

  override fun Message.predicate() = text?.startsWith('+') ?: false
}