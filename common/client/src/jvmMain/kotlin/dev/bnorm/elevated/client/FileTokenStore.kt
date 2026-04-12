package dev.bnorm.elevated.client

import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class FileTokenStore(
    private val path: Path,
) : TokenStore {
    companion object {
        val DEFAULT = FileTokenStore(Path(".elevated/token.jwt"))
    }

    override var authorization: String?
        get() {
            synchronized(path) {
                return when (path.exists()) {
                    true -> path.readText()
                    false -> null
                }
            }
        }
        set(value) {
            synchronized(path) {
                when (value) {
                    null -> path.deleteIfExists()
                    else -> {
                        path.createParentDirectories()
                        path.writeText(value)
                    }
                }
            }
        }
}
