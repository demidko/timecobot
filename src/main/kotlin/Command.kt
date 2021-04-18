import kotlin.time.Duration

sealed class Command {

  object Status : Command()

  class Mutable(action: timeql.Mutable, duration: Duration) : Command()
}