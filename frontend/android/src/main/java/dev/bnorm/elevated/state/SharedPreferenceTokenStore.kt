package dev.bnorm.elevated.state

import android.content.SharedPreferences
import dev.bnorm.elevated.client.TokenStore

class SharedPreferenceTokenStore(
    private val preferences: SharedPreferences
) : TokenStore {
    override var authorization: String?
        get() = preferences.getString("TOKEN", null)
        set(value) {
            preferences.edit().putString("TOKEN", value).apply()
        }
}
