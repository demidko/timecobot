fun main(args: Array<String>) {
  newWebserver().start(false)
  newTimecobot(args[0], args[1]).startPolling()
}

