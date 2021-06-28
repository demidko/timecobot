package ml.demidko.timecobot.semnorms.commands

import ml.demidko.timecobot.Query
import ml.demidko.timecobot.semnorms.Executable
import ml.demidko.timecobot.semnorms.stem

/** Skip telegram-command prefix '/' */
object Skip : Executable(stem("/")) {
  override fun execute(query: Query) = query.execute()
}