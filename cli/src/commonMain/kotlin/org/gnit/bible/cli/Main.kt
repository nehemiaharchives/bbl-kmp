package org.gnit.bible.cli

import org.gnit.bible.Bible

fun main() {

    val verses = Bible().verses()

    println("genesis 1:$verses")
}
