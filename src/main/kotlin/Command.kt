import kotlin.time.Duration

sealed class Command {

  object CheckStatus : Command()

  class Transfer(val duration: Duration)

  class Ban(val duration: Duration)

  class Redeem(val duration: Duration)
}
