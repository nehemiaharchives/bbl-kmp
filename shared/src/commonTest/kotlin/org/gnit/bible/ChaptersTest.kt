package org.gnit.bible

import kotlin.test.Test
import kotlin.test.assertEquals

class ChaptersTest {

    @Test
    fun testMaxChapters() {
        assertEquals(50, Chapters.maxChapter(1)) // Genesis
        assertEquals(150, Chapters.maxChapter(19)) // Psalms
    }
}