package org.gnit.bible.cli

import kotlin.system.exitProcess

fun main(vararg args: String) {
    if (args.isEmpty()) {
        println("bbl <command> [options]\n\nCommands:\n  create index <id>   Create a lucene index for a translation id (placeholder)\n")
        return
    }
    when (args.first()) {
        "create" -> handleCreate(args.drop(1))
        else -> {
            println("Unknown command: ${args.first()}")
            exitProcess(1)
        }
    }
}

private fun handleCreate(args: List<String>) {
    if (args.size >= 2 && args[0] == "index") {
        val id = args[1]
        println("[stub] Would create index for translation '$id' (implement lucene-kmp integration here)")
    } else {
        println("Usage: bbl create index <translationId>")
    }
}
