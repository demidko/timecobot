package ml.demidko.timecobot

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import com.github.demidko.tokenizer.tokenize

class Command(private val words: List<WordformMeaning?>)
