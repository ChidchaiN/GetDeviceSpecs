package org.example.getdevicespecs

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform