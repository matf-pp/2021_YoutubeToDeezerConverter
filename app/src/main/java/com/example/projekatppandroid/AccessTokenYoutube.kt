package com.example.projekatppandroid

import kotlinx.serialization.Serializable


@Serializable
data class Success
(
    var Success : AccessTokenYoutube
){}

@Serializable
data class AccessTokenYoutube
(
    var access_token : String,
    var expires_in : String,
    var id_token : String
    //var scope : String,
    //var id_token : String,
    //var refresh_token : String
)
