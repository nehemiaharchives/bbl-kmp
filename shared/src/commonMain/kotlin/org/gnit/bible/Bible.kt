package org.gnit.bible

class Bible {
    private val platform = getPlatform()

    fun verses(): String {
        return "Genesis 1:1 In the beginning, God created the heavens and the earth."
    }
}