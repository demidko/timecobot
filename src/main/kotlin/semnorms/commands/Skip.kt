package semnorms.commands

import Query
import semnorms.Executable
import semnorms.stem

/** Skip telegram-command prefix '/' */
object Skip : Executable(stem("/")) {
  override fun execute(query: Query) = query.execute()
}