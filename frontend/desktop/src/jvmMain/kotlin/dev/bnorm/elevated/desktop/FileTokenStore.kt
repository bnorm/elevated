package dev.bnorm.elevated.desktop

import dev.bnorm.elevated.client.TokenStore
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
        fun default(): FileTokenStore {
            val path = Path(".elevated/token.jwt")
            path.createParentDirectories()
            return FileTokenStore(path)
        }
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
                    else -> path.writeText(value)
                }
            }
        }
}
