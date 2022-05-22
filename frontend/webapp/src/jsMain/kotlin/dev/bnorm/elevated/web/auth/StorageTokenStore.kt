package dev.bnorm.elevated.web.auth

import dev.bnorm.elevated.client.TokenStore
import org.w3c.dom.Storage

class StorageTokenStore(
    private val storage: Storage,
) : TokenStore {
    companion object {
        private const val KEY = "TOKEN"
    }

    override var authorization: String?
        get() = storage.getItem(KEY)
        set(value) {
            if (value == null) {
                storage.removeItem(KEY)
            } else {
                storage.setItem(KEY, value)
            }
        }
}
