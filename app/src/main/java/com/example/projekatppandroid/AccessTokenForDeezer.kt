package com.example.projekatppandroid

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenForDeezer
(
    private val access_token: String,
    private val expires: String
) {
    fun getAccess_token() : String{
        return access_token
    }

    fun getExpires() : String{
        return expires
    }
}