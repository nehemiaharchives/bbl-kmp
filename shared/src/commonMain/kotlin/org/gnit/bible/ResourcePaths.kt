package org.gnit.bible

object ResourcePaths {
    private const val BASE = "bblpacks"

    fun chapterFile(translation: String, book: Int, chapter: Int): String =
        "files/$BASE/$translation/$translation.$book.$chapter.txt"
}