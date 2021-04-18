import speech.MutableAction
import kotlin.time.Duration

sealed class Command {

  object StatusCommand : Command()

  object NotAction : Command()

  data class MutableCommand(val action: MutableAction, val duration: Duration) : Command()
}