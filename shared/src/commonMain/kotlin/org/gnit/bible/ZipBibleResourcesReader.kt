package org.gnit.bible

import io.github.oshai.kotlinlogging.KotlinLogging
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.openZip
import okio.use

class ZipBibleResourcesReader(
    val platform: Platform,
    private val fileSystem: FileSystem = platform.fileSystem
) : BibleResourcesReader {

    val logger = KotlinLogging.logger {}

    override fun chapterFile(translation: String, book: Int, chapter: Int): String {
        throw UnsupportedOperationException()
    }

    override fun readByPath(path: String): String {
        throw UnsupportedOperationException()
    }

    override fun getChapterText(translation: String, book: Int, chapter: Int): String {
        return withZipFile(translation) { zip ->
            val expectedFileName = "$translation.$book.$chapter.txt"
            val targetName = zip.listFilesRecursively()
                .firstOrNull { it.name.substringAfterLast('/') == expectedFileName }
                ?: error("No entry ending with $expectedFileName found")
            readEntryBytes(zip, targetName).decodeToString()
        }
    }

    override fun listIndexFiles(translation: String): List<String> {
        return withZipFile(translation) { zip ->
            zip.listFilesRecursively()
                .map { it.toString().removePrefix("/") }
                .filter { it.startsWith("index/") && !it.endsWith("/") }
                .map { it.removePrefix("index/") }
                .toList()
        }
    }

    override fun readIndexFile(translation: String, name: String): ByteArray {
        require(name.isNotBlank()) { "Index file name is blank" }
        require(!name.contains('/')) { "Index file name must be a flat filename, got: $name" }
        require(!name.contains('\\')) { "Index file name must be a flat filename, got: $name" }
        require(!name.contains("..")) { "Index file name must not contain '..', got: $name" }

        val target = "index/$name"
        return withZipFile(translation) { zip ->
            val entry = zip.listFilesRecursively().firstOrNull { it.toString().removePrefix("/") == target }
                ?: error("Index file not found in zip: $target")
            readEntryBytes(zip, entry)
        }
    }

    fun getTranslationFromManifest(translationCode: String): Translation {
        val json = withZipFile(translationCode) { zip ->
            val manifest = "$translationCode$MANIFEST_JSON_POSTFIX"
            val targetName = zip.listFilesRecursively().firstOrNull { it.name.endsWith(manifest) }
                ?: error("$manifest not found in $zip")
            readEntryBytes(zip, targetName).decodeToString()
        }
        return Translation.fromJson(json)
    }

    private inline fun <T> withZipFile(
        translationCode: String,
        block: (FileSystem) -> T
    ): T {
        val zipPath = platform.packDir.toPath() / "$translationCode.zip"
        require(fileSystem.exists(zipPath)) { "ZipBibleResourcesReader Zip file not found at $zipPath" }
        val zipFileSystem = fileSystem.openZip(zipPath)
        logger.debug { "ZipBibleResourcesReader successfully found and opened $zipPath" }
        return block(zipFileSystem)
    }

    private fun FileSystem.listFilesRecursively(dir: Path = "/".toPath()): List<Path> {
        val result = mutableListOf<Path>()
        listOrNull(dir).orEmpty().forEach { child ->
            val metadata = metadataOrNull(child)
            if (metadata?.isDirectory == true) {
                result += listFilesRecursively(child)
            } else {
                result += child
            }
        }
        return result
    }

    private fun readEntryBytes(zip: FileSystem, path: Path): ByteArray {
        return zip.source(path).buffer().use { it.readByteArray() }
    }
}
