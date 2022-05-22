package dev.bnorm.elevated.client

import dev.bnorm.elevated.model.auth.AuthorizationToken
import dev.bnorm.elevated.model.auth.JwtTokenUsage

interface TokenStore {
    var authorization: String?

    @OptIn(JwtTokenUsage::class)
    fun setAuthorization(token: AuthorizationToken) {
        authorization = "${token.type} ${token.value.value}"
    }

    class Memory : TokenStore {
        override var authorization: String? = null
    }
}
