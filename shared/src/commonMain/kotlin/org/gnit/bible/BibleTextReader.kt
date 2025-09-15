package org.gnit.bible

interface BibleTextReader {

    fun readByPath(path: String): String

}

fun BibleTextReader.getChapterText(translation: String, book: Int, chapter: Int): String {
    val path = ResourcePaths.chapterFile(translation, book, chapter)
    return readByPath(path)
}
