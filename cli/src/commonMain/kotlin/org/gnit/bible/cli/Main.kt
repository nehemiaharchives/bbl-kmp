package org.gnit.bible.cli

import org.gnit.bible.Bible
import com.github.ajalt.clikt.core.CoreCliktCommand
import com.github.ajalt.clikt.core.main

class Bbl: CoreCliktCommand() {

    val bible = Bible()

    override fun run() {
        echo(bible.verses())
    }
}

fun main(args: Array<String>) {
    Bbl().main(args)
}
