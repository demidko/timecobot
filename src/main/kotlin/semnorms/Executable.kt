package semnorms

import Query

abstract class Executable(vararg rules: Rule) : Semnorm(*rules) {

  abstract fun execute(query: Query)
}