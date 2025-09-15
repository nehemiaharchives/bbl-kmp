package org.gnit.bible

import kotlin.test.Test
import kotlin.test.assertEquals

class ResourcePathsTest {

    @Test
    fun testChapterFilePath() {
        val path = ResourcePaths.chapterFile("kjv", 1, 1)
        assertEquals("bblpacks/kjv/kjv.1.1.txt", path)
    }
}