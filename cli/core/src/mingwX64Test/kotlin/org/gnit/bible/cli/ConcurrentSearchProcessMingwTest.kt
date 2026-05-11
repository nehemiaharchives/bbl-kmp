package org.gnit.bible.cli

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class ConcurrentSearchProcessMingwTest {

    @Test
    fun concurrentInstalledSearchProcessesCanReadPacks() {
        val fileSystem = FileSystem.SYSTEM
        val installRoot = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "bbl-concurrent-search-${Random.nextLong().toString(16)}"
        val bblRoot = installRoot / ".bbl"
        val binDir = bblRoot / "bin"
        val packDir = bblRoot / "packs"
        val fixtureDir = "../../bbl_install_windows/files".toPath()

        try {
            fileSystem.createDirectories(binDir)
            fileSystem.createDirectories(packDir)
            copyMatching(fileSystem, fixtureDir, binDir) { it.endsWith(".exe") }
            copyMatching(fileSystem, fixtureDir, packDir) { it.endsWith(".zip") }

            val script = concurrentSearchScript(
                localAppData = installRoot.toString(),
                bblExe = (binDir / "bbl.exe").toString(),
                outputDir = installRoot.toString(),
            )
            val result = PlatformProcessRunner().run(
                listOf(
                    "powershell.exe",
                    "-NoProfile",
                    "-ExecutionPolicy",
                    "Bypass",
                    "-Command",
                    script,
                )
            )

            assertEquals(0, result.exitCode, "stdout:\n${result.stdout}\nstderr:\n${result.stderr}")
        } finally {
            runCatching { fileSystem.deleteRecursively(installRoot) }
        }
    }

    private fun copyMatching(
        fileSystem: FileSystem,
        sourceDir: Path,
        targetDir: Path,
        predicate: (String) -> Boolean,
    ) {
        fileSystem.list(sourceDir)
            .filter { predicate(it.name) }
            .forEach { source ->
                fileSystem.copy(source, targetDir / source.name)
            }
    }

    private fun concurrentSearchScript(localAppData: String, bblExe: String, outputDir: String): String {
        val escapedLocalAppData = localAppData.replace("'", "''")
        val escapedBblExe = bblExe.replace("'", "''")
        val escapedOutputDir = outputDir.replace("'", "''")
        return """
            ${'$'}ErrorActionPreference = 'Stop'
            ${'$'}env:LOCALAPPDATA = '$escapedLocalAppData'
            ${'$'}bbl = '$escapedBblExe'
            ${'$'}out1 = Join-Path '$escapedOutputDir' 'webus.out'
            ${'$'}err1 = Join-Path '$escapedOutputDir' 'webus.err'
            ${'$'}out2 = Join-Path '$escapedOutputDir' 'kjv.out'
            ${'$'}err2 = Join-Path '$escapedOutputDir' 'kjv.err'
            ${'$'}psi1 = [System.Diagnostics.ProcessStartInfo]::new(${'$'}bbl, 'search Jesus Christ')
            ${'$'}psi1.UseShellExecute = ${'$'}false
            ${'$'}psi1.RedirectStandardOutput = ${'$'}true
            ${'$'}psi1.RedirectStandardError = ${'$'}true
            ${'$'}psi2 = [System.Diagnostics.ProcessStartInfo]::new(${'$'}bbl, 'search Jesus Christ in kjv')
            ${'$'}psi2.UseShellExecute = ${'$'}false
            ${'$'}psi2.RedirectStandardOutput = ${'$'}true
            ${'$'}psi2.RedirectStandardError = ${'$'}true
            ${'$'}p1 = [System.Diagnostics.Process]::Start(${'$'}psi1)
            ${'$'}p2 = [System.Diagnostics.Process]::Start(${'$'}psi2)
            ${'$'}stdout1Task = ${'$'}p1.StandardOutput.ReadToEndAsync()
            ${'$'}stderr1Task = ${'$'}p1.StandardError.ReadToEndAsync()
            ${'$'}stdout2Task = ${'$'}p2.StandardOutput.ReadToEndAsync()
            ${'$'}stderr2Task = ${'$'}p2.StandardError.ReadToEndAsync()
            ${'$'}p1.WaitForExit()
            ${'$'}p2.WaitForExit()
            Set-Content -Path ${'$'}out1 -Value ${'$'}stdout1Task.Result
            Set-Content -Path ${'$'}err1 -Value ${'$'}stderr1Task.Result
            Set-Content -Path ${'$'}out2 -Value ${'$'}stdout2Task.Result
            Set-Content -Path ${'$'}err2 -Value ${'$'}stderr2Task.Result
            Write-Output "EXIT1=${'$'}(${'$'}p1.ExitCode)"
            Write-Output "EXIT2=${'$'}(${'$'}p2.ExitCode)"
            Write-Output "WEBUS_ERR=$(Get-Content -Raw ${'$'}err1)"
            Write-Output "KJV_ERR=$(Get-Content -Raw ${'$'}err2)"
            if (${'$'}p1.ExitCode -ne 0 -or ${'$'}p2.ExitCode -ne 0) { exit 1 }
        """.trimIndent()
    }
}
