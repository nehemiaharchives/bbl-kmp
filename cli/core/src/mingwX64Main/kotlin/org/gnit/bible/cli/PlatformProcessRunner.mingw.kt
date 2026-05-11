package org.gnit.bible.cli

actual class PlatformProcessRunner actual constructor() : ProcessRunner {
    override fun run(command: List<String>): ProcessResult {
        require(command.isNotEmpty()) { "command must not be empty" }
        return ProcessResult(
            exitCode = 127,
            stdout = "",
            stderr = "Process execution is not implemented for mingwX64 yet: ${command.joinToString(" ")}"
        )
    }
}
