package com.gib.filrouge.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Data class meant to encapsulate the login details
// provided by the user for sending to the API.
@Serializable
data class LoginForm(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String
)
