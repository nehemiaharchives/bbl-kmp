package org.gnit.bible

object ResourcePaths {
    private const val BASE = "bblpacks"

    fun chapterFile(translation: String, book: Int, chapter: Int): String =
        "$BASE/$translation/$translation.$book.$chapter.txt"
}