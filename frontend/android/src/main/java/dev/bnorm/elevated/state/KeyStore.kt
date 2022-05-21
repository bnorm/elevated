package dev.bnorm.elevated.state

import android.content.SharedPreferences
import dev.bnorm.elevated.model.auth.AuthorizationToken
import dev.bnorm.elevated.model.auth.JwtTokenUsage

class KeyStore(
    private val preferences: SharedPreferences
) {
    var authorization: String?
        get() = preferences.getString("TOKEN", null)
        set(value) {
            preferences.edit().putString("TOKEN", value).apply()
        }

    @OptIn(JwtTokenUsage::class)
    fun setAuthorization(token: AuthorizationToken) {
        authorization = "${token.type} ${token.value.value}"
    }
}