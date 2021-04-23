package com.example.projekatppandroid

import kotlinx.serialization.Serializable

@Serializable
data class CredentialsForGoogle(
    val web: Web
)

@Serializable
data class Web(
    val auth_provider_x509_cert_url: String,
    val auth_uri: String,
    val client_id: String,
    val client_secret: String,
    val project_id: String,
    val token_uri: String
)

