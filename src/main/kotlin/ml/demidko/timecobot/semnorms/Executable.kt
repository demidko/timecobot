package ml.demidko.timecobot.semnorms

import ml.demidko.timecobot.Query

abstract class Executable(vararg rules: Rule) : Semnorm(*rules) {

  abstract fun execute(query: Query)
}