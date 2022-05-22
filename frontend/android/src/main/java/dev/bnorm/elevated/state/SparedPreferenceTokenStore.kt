package dev.bnorm.elevated.state

import android.content.SharedPreferences
import dev.bnorm.elevated.client.TokenStore

class SparedPreferenceTokenStore(
    private val preferences: SharedPreferences
) : TokenStore {
    override var authorization: String?
        get() = preferences.getString("TOKEN", null)
        set(value) {
            preferences.edit().putString("TOKEN", value).apply()
        }
}
