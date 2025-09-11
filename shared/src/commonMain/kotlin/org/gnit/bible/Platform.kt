package org.gnit.bible

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform